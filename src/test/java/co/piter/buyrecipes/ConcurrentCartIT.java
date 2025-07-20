package co.piter.buyrecipes;

import co.piter.buyrecipes.dto.*;
import co.piter.buyrecipes.service.CartService;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static co.piter.buyrecipes.TutorialIT.*;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConcurrentCartIT {

    private static @Nullable Long aliceCartId;
    private static @Nullable Long bobCartId;

    @Inject
    @Client("/")
    @NotNull HttpClient client;

    @Inject
    @NotNull CartService cartService;

    private static @NotNull Long findProductIdByName(final @NotNull List<ProductDto> products, final @NotNull String name) {
        return products.stream()
                .filter(product -> product.getName().equals(name))
                .map(ProductDto::getId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found: " + name));
    }

    @Test
    @Order(1)
    void setupPaellaRecipeAndIngredients() {
        for (String ingredient : INGREDIENTS) {
            createProduct(client, ingredient, PRICES.get(ingredient));
        }

        final CreateRecipeRequest request = new CreateRecipeRequest(NAME, DESCRIPTION);
        final HttpResponse<RecipeDto> response = client.toBlocking()
                .exchange(HttpRequest.POST("/recipes", request), RecipeDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertThat(response.getBody().isPresent()).isTrue();
        paellaRecipeId = response.getBody().get().getId();

        final List<ProductDto> products = client.toBlocking()
                .retrieve(HttpRequest.GET("/products"), Argument.listOf(ProductDto.class));

        for (String ingredient : INGREDIENTS) {
            final Long productId = findProductIdByName(products, ingredient);
            createRecipeIngredient(client, paellaRecipeId, productId, QUANTITIES.get(ingredient), UNITS.get(ingredient));
        }

        final RecipeDto completeRecipe = client.toBlocking()
                .retrieve(HttpRequest.GET("/recipes/" + paellaRecipeId), RecipeDto.class);
        assertThat(completeRecipe.getIngredients()).hasSize(INGREDIENTS.length);
    }

    @Test
    @Order(2)
    void setupTwoCarts() {
        final HttpResponse<CartDto> aliceResponse = client.toBlocking()
                .exchange(HttpRequest.POST("/carts", new CreateCartRequest(0)), CartDto.class);
        assertEquals(HttpStatus.OK, aliceResponse.getStatus());
        assertThat(aliceResponse.getBody().isPresent()).isTrue();
        aliceCartId = aliceResponse.getBody().get().getId();

        final HttpResponse<CartDto> bobResponse = client.toBlocking()
                .exchange(HttpRequest.POST("/carts", new CreateCartRequest(0)), CartDto.class);
        assertEquals(HttpStatus.OK, bobResponse.getStatus());
        assertThat(bobResponse.getBody().isPresent()).isTrue();
        bobCartId = bobResponse.getBody().get().getId();

        assertThat(aliceCartId).isNotNull();
        assertThat(bobCartId).isNotNull();
        assertThat(aliceCartId).isNotEqualTo(bobCartId);
    }

    @Test
    @Order(3)
    void demonstrateSuccessfulConcurrentCartOperations() throws InterruptedException, ExecutionException, TimeoutException {
        final ExecutorService executor = Executors.newFixedThreadPool(2);
        final CompletableFuture<Boolean> aliceTask = CompletableFuture.supplyAsync(() -> {
            try {
                final HttpResponse<CartDto> response = client.toBlocking()
                        .exchange(HttpRequest.POST(
                                "/carts/" + aliceCartId + "/add_recipe",
                                new AddRecipeRequest(requireNonNull(paellaRecipeId))), CartDto.class);
                return response.getStatus() == HttpStatus.OK;
            } catch (Exception e) {
                return false;
            }
        }, executor);

        final CompletableFuture<Boolean> bobTask = CompletableFuture.supplyAsync(() -> {
            try {
                final HttpResponse<CartDto> response = client.toBlocking()
                        .exchange(HttpRequest.POST(
                                "/carts/" + bobCartId + "/add_recipe",
                                new AddRecipeRequest(requireNonNull(paellaRecipeId))), CartDto.class);
                return response.getStatus() == HttpStatus.OK;
            } catch (Exception e) {
                return false;
            }
        }, executor);

        final Boolean aliceSuccess = aliceTask.get(5, TimeUnit.SECONDS);
        final Boolean bobSuccess = bobTask.get(5, TimeUnit.SECONDS);

        executor.shutdown();

        assertThat(aliceSuccess).isTrue();
        assertThat(bobSuccess).isTrue();

        final CartDto aliceCart = client.toBlocking()
                .retrieve(HttpRequest.GET("/carts/" + aliceCartId), CartDto.class);
        final CartDto bobCart = client.toBlocking()
                .retrieve(HttpRequest.GET("/carts/" + bobCartId), CartDto.class);

        assertThat(aliceCart.getItems()).hasSize(INGREDIENTS.length);
        assertThat(bobCart.getItems()).hasSize(INGREDIENTS.length);
        assertThat(aliceCart.getTotalInCents()).isGreaterThan(0);
        assertThat(bobCart.getTotalInCents()).isGreaterThan(0);
        assertThat(aliceCart.getTotalInCents()).isEqualTo(bobCart.getTotalInCents());
    }

    @Test
    @Order(4)
    void demonstrateConcurrentCartUpdatesWithOptimisticLocking() throws InterruptedException, ExecutionException, TimeoutException {
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        final AtomicInteger successCount = new AtomicInteger(0);
        final AtomicInteger conflictCount = new AtomicInteger(0);
        final AtomicReference<Exception> unexpectedException = new AtomicReference<>();

        final CompletableFuture<?>[] futures = new CompletableFuture[10];
        for (int i = 0; i < 10; i++) {
            final int attemptNumber = i;
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    // Each thread tries to update the cart total to a different value
                    final UpdateCartRequest request = new UpdateCartRequest(1000 + attemptNumber * 100);
                    final var result = cartService.updateCart(requireNonNull(aliceCartId), request);
                    if (result.isPresent()) {
                        successCount.incrementAndGet();
                    }
                } catch (OptimisticLockException e) {
                    conflictCount.incrementAndGet();
                } catch (Exception e) {
                    unexpectedException.set(e);
                }
            }, executor);
        }

        CompletableFuture.allOf(futures).get(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(successCount.get()).isGreaterThanOrEqualTo(1);
        assertThat(unexpectedException.get()).isNull();
        assertThat(successCount.get() + conflictCount.get()).isEqualTo(10);

        final CartDto finalCart = client.toBlocking()
                .retrieve(HttpRequest.GET("/carts/" + aliceCartId), CartDto.class);
        assertThat(finalCart).isNotNull();
        assertThat(finalCart.getTotalInCents()).isGreaterThan(0);
    }

    @Test
    @Order(5)
    void demonstrateVersionFieldIncrements() {
        final CartDto initialCart = client.toBlocking()
                .retrieve(HttpRequest.GET("/carts/" + bobCartId), CartDto.class);
        final int initialTotal = initialCart.getTotalInCents();

        int expectedTotal = initialTotal;
        for (int i = 1; i <= 5; i++) {
            expectedTotal += 100;
            final UpdateCartRequest request = new UpdateCartRequest(expectedTotal);

            final HttpResponse<CartDto> response = client.toBlocking()
                    .exchange(HttpRequest.PUT("/carts/" + bobCartId, request), CartDto.class);
            assertEquals(HttpStatus.OK, response.getStatus());
            assertThat(response.getBody().isPresent()).isTrue();
            assertThat(response.getBody().get().getTotalInCents()).isEqualTo(expectedTotal);
        }

        assertThat(client.toBlocking()
                .retrieve(HttpRequest.GET("/carts/" + bobCartId), CartDto.class)
                .getTotalInCents())
                .isEqualTo(initialTotal + 500);
    }
}