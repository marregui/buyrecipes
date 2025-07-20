package co.piter.buyrecipes;

import co.piter.buyrecipes.dto.*;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@MicronautTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TutorialIT {

    // https://www.consum.es/entrenosotros/paella/

    static final @NotNull String NAME = "Paella Valenciana";
    static final @NotNull String DESCRIPTION = "Traditional Spanish paella for 4 people with chicken, rabbit, and vegetables";

    static final @NotNull String RICE = "Arroz redondo";
    static final @NotNull String CHICKEN = "Carne de pollo";
    static final @NotNull String RABBIT = "Carne de conejo";
    static final @NotNull String TOMATO = "Tomate maduro";
    static final @NotNull String GREEN_BEANS = "Bajoqueta (judía verde plana)";
    static final @NotNull String LIMA_BEANS = "Garrofó";
    static final @NotNull String OLIVE_OIL = "Aceite de oliva";
    static final @NotNull String SWEET_PAPRIKA = "Pimentón dulce";
    static final @NotNull String SAFFRON = "Azafrán";
    static final @NotNull String WATER = "Agua";
    static final @NotNull String SALT = "Sal";

    static final @NotNull String @NotNull [] INGREDIENTS = {
            RICE,
            CHICKEN,
            RABBIT,
            TOMATO,
            GREEN_BEANS,
            LIMA_BEANS,
            OLIVE_OIL,
            SWEET_PAPRIKA,
            SAFFRON,
            WATER,
            SALT
    };

    // in cents
    static final @NotNull Map<String, Integer> PRICES = Map.ofEntries(
            Map.entry(RICE, 250),           // €2.50 per package
            Map.entry(CHICKEN, 890),        // €8.90 per 600g
            Map.entry(RABBIT, 1200),        // €12.00 per 400g
            Map.entry(TOMATO, 80),          // €0.80 per piece
            Map.entry(GREEN_BEANS, 320),    // €3.20 per 150g
            Map.entry(LIMA_BEANS, 450),     // €4.50 per 150g
            Map.entry(OLIVE_OIL, 180),      // €1.80 per 2 tablespoons
            Map.entry(SWEET_PAPRIKA, 120),  // €1.20 per teaspoon
            Map.entry(SAFFRON, 850),        // €8.50 per pinch
            Map.entry(WATER, 0),            // Free
            Map.entry(SALT, 50)             // €0.50 per portion
    );

    static final @NotNull Map<String, Integer> QUANTITIES = Map.ofEntries(
            Map.entry(RICE, 350),           // 350g
            Map.entry(CHICKEN, 600),        // 600g
            Map.entry(RABBIT, 400),         // 400g
            Map.entry(TOMATO, 1),           // 1 piece
            Map.entry(GREEN_BEANS, 150),    // 150g
            Map.entry(LIMA_BEANS, 150),     // 150g
            Map.entry(OLIVE_OIL, 2),        // 2 tablespoons
            Map.entry(SWEET_PAPRIKA, 1),    // 1 teaspoon
            Map.entry(SAFFRON, 1),          // 1 pinch
            Map.entry(WATER, 3),            // 3 cups
            Map.entry(SALT, 1)              // to taste
    );

    static final @NotNull Map<String, String> UNITS = Map.ofEntries(
            Map.entry(RICE, "grams"),
            Map.entry(CHICKEN, "grams"),
            Map.entry(RABBIT, "grams"),
            Map.entry(TOMATO, "pieces"),
            Map.entry(GREEN_BEANS, "grams"),
            Map.entry(LIMA_BEANS, "grams"),
            Map.entry(OLIVE_OIL, "tablespoons"),
            Map.entry(SWEET_PAPRIKA, "teaspoons"),
            Map.entry(SAFFRON, "pinches"),
            Map.entry(WATER, "cups"),
            Map.entry(SALT, "to taste")
    );

    static @Nullable Long paellaRecipeId;
    private static @Nullable Long cartId;

    @Inject
    @Client("/")
    @NotNull HttpClient client;

    private static @NotNull Long findProductIdByName(
            final @NotNull List<ProductDto> products,
            final @NotNull String name
    ) {
        return products.stream()
                .filter(product -> product.getName().equals(name))
                .map(ProductDto::getId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found: " + name));
    }

    static void createRecipeIngredient(
            final @NotNull HttpClient client,
            final @Nullable Long recipeId,
            final @NotNull Long productId,
            final @NotNull Integer quantity,
            final @NotNull String unit) {
        if (recipeId == null) {
            fail("recipeId is null");
        }
        final HttpResponse<RecipeIngredientDto> response = client.toBlocking()
                .exchange(
                        HttpRequest.POST(
                                "/recipe-ingredients",
                                new CreateRecipeIngredientRequest(
                                        recipeId,
                                        productId,
                                        quantity,
                                        unit)),
                        RecipeIngredientDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertThat(response.getBody().isPresent()).isTrue();
    }

    static @NotNull ProductDto createProduct(
            final @NotNull HttpClient client,
            final @NotNull String name,
            final @NotNull Integer priceInCents
    ) {
        final HttpResponse<ProductDto> response = client.toBlocking()
                .exchange(HttpRequest.POST("/products", new CreateProductRequest(name, priceInCents)), ProductDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertThat(response.getBody().isPresent()).isTrue();
        return response.getBody().get();
    }

    @Test
    @Order(1)
    void step1_verifyApiIsRunning() {
        final HttpResponse<List<ProductDto>> response = client.toBlocking()
                .exchange(HttpRequest.GET("/products"), Argument.listOf(ProductDto.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertThat(response.getBody().isPresent()).isTrue();
    }

    @Test
    @Order(2)
    void step2_createAllPaellaIngredients() {
        final ProductDto rice = createProduct(client, RICE, PRICES.get(RICE));
        final ProductDto chicken = createProduct(client, CHICKEN, PRICES.get(CHICKEN));
        final ProductDto rabbit = createProduct(client, RABBIT, PRICES.get(RABBIT));
        final ProductDto tomato = createProduct(client, TOMATO, PRICES.get(TOMATO));
        final ProductDto greenBeans = createProduct(client, GREEN_BEANS, PRICES.get(GREEN_BEANS));
        final ProductDto limaBeans = createProduct(client, LIMA_BEANS, PRICES.get(LIMA_BEANS));
        final ProductDto oliveOil = createProduct(client, OLIVE_OIL, PRICES.get(OLIVE_OIL));
        final ProductDto paprika = createProduct(client, SWEET_PAPRIKA, PRICES.get(SWEET_PAPRIKA));
        final ProductDto saffron = createProduct(client, SAFFRON, PRICES.get(SAFFRON));
        final ProductDto water = createProduct(client, WATER, PRICES.get(WATER));
        final ProductDto salt = createProduct(client, SALT, PRICES.get(SALT));

        assertThat(rice.getName()).isEqualTo(RICE);
        assertThat(chicken.getName()).isEqualTo(CHICKEN);
        assertThat(rabbit.getName()).isEqualTo(RABBIT);
        assertThat(tomato.getName()).isEqualTo(TOMATO);
        assertThat(greenBeans.getName()).isEqualTo(GREEN_BEANS);
        assertThat(limaBeans.getName()).isEqualTo(LIMA_BEANS);
        assertThat(oliveOil.getName()).isEqualTo(OLIVE_OIL);
        assertThat(paprika.getName()).isEqualTo(SWEET_PAPRIKA);
        assertThat(saffron.getName()).isEqualTo(SAFFRON);
        assertThat(water.getName()).isEqualTo(WATER);
        assertThat(salt.getName()).isEqualTo(SALT);

        assertThat(rice.getPriceInCents()).isEqualTo(PRICES.get(RICE));
        assertThat(chicken.getPriceInCents()).isEqualTo(PRICES.get(CHICKEN));
        assertThat(rabbit.getPriceInCents()).isEqualTo(PRICES.get(RABBIT));
        assertThat(water.getPriceInCents()).isEqualTo(PRICES.get(WATER));
        assertThat(saffron.getPriceInCents()).isEqualTo(PRICES.get(SAFFRON));
    }

    @Test
    @Order(3)
    void step3_createPaellaRecipe() {
        final CreateRecipeRequest request = new CreateRecipeRequest(NAME, DESCRIPTION);

        final HttpResponse<RecipeDto> response = client.toBlocking()
                .exchange(HttpRequest.POST("/recipes", request), RecipeDto.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertThat(response.getBody().isPresent()).isTrue();

        final RecipeDto recipe = response.getBody().get();
        paellaRecipeId = recipe.getId();

        assertThat(recipe.getName()).isEqualTo(NAME);
        assertThat(recipe.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(paellaRecipeId).isNotNull();
    }

    @Test
    @Order(4)
    void step4_addIngredientsToRecipe() {
        final List<ProductDto> products = client.toBlocking()
                .retrieve(HttpRequest.GET("/products"), Argument.listOf(ProductDto.class));
        assertThat(products).hasSizeGreaterThanOrEqualTo(INGREDIENTS.length);

        final Long riceId = findProductIdByName(products, RICE);
        final Long chickenId = findProductIdByName(products, CHICKEN);
        final Long rabbitId = findProductIdByName(products, RABBIT);
        final Long tomatoId = findProductIdByName(products, TOMATO);
        final Long greenBeansId = findProductIdByName(products, GREEN_BEANS);
        final Long limaBeansId = findProductIdByName(products, LIMA_BEANS);
        final Long oliveOilId = findProductIdByName(products, OLIVE_OIL);
        final Long paprikaId = findProductIdByName(products, SWEET_PAPRIKA);
        final Long saffronId = findProductIdByName(products, SAFFRON);
        final Long waterId = findProductIdByName(products, WATER);
        final Long saltId = findProductIdByName(products, SALT);

        createRecipeIngredient(client, paellaRecipeId, riceId, QUANTITIES.get(RICE), UNITS.get(RICE));
        createRecipeIngredient(client, paellaRecipeId, chickenId, QUANTITIES.get(CHICKEN), UNITS.get(CHICKEN));
        createRecipeIngredient(client, paellaRecipeId, rabbitId, QUANTITIES.get(RABBIT), UNITS.get(RABBIT));
        createRecipeIngredient(client, paellaRecipeId, tomatoId, QUANTITIES.get(TOMATO), UNITS.get(TOMATO));
        createRecipeIngredient(client, paellaRecipeId, greenBeansId, QUANTITIES.get(GREEN_BEANS), UNITS.get(GREEN_BEANS));
        createRecipeIngredient(client, paellaRecipeId, limaBeansId, QUANTITIES.get(LIMA_BEANS), UNITS.get(LIMA_BEANS));
        createRecipeIngredient(client, paellaRecipeId, oliveOilId, QUANTITIES.get(OLIVE_OIL), UNITS.get(OLIVE_OIL));
        createRecipeIngredient(client, paellaRecipeId, paprikaId, QUANTITIES.get(SWEET_PAPRIKA), UNITS.get(SWEET_PAPRIKA));
        createRecipeIngredient(client, paellaRecipeId, saffronId, QUANTITIES.get(SAFFRON), UNITS.get(SAFFRON));
        createRecipeIngredient(client, paellaRecipeId, waterId, QUANTITIES.get(WATER), UNITS.get(WATER));
        createRecipeIngredient(client, paellaRecipeId, saltId, QUANTITIES.get(SALT), UNITS.get(SALT));

        final RecipeDto completeRecipe = client.toBlocking()
                .retrieve(HttpRequest.GET("/recipes/" + paellaRecipeId), RecipeDto.class);

        assertThat(completeRecipe.getIngredients()).hasSize(INGREDIENTS.length);
    }

    @Test
    @Order(5)
    void step5_createShoppingCart() {
        final HttpResponse<CartDto> response = client.toBlocking()
                .exchange(HttpRequest.POST("/carts", new CreateCartRequest(0)), CartDto.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertThat(response.getBody().isPresent()).isTrue();

        final CartDto cart = response.getBody().get();
        cartId = cart.getId();
        assertThat(cart.getTotalInCents()).isEqualTo(0);
        assertThat(cartId).isNotNull();
    }

    @Test
    @Order(6)
    void step6_addPaellaRecipeToCart() {
        assertEquals(HttpStatus.OK, client.toBlocking()
                .exchange(HttpRequest.POST(
                        "/carts/" + cartId + "/add_recipe",
                        new AddRecipeRequest(requireNonNull(paellaRecipeId))))
                .getStatus());
    }

    @Test
    @Order(7)
    void step7_verifyCartContainsAllIngredients() {
        final CartDto cart = client.toBlocking()
                .retrieve(HttpRequest.GET("/carts/" + cartId), CartDto.class);
        assertThat(cart.getItems()).hasSize(INGREDIENTS.length);
        assertThat(cart.getTotalInCents()).isGreaterThan(0);

        final List<String> ingredientNames = cart.getItems().stream()
                .map(ProductDto::getName)
                .toList();

        assertThat(ingredientNames).contains(INGREDIENTS);
    }

    @Test
    @Order(8)
    void step8_verifyCompleteRecipeWithIngredients() {
        final RecipeDto recipe = client.toBlocking()
                .retrieve(HttpRequest.GET("/recipes/" + paellaRecipeId), RecipeDto.class);

        assertThat(recipe.getName()).isEqualTo(NAME);
        assertThat(recipe.getDescription()).contains(DESCRIPTION);
        assertThat(recipe.getIngredients()).hasSize(INGREDIENTS.length);
        assertThat(recipe.getIngredients()
                .stream()
                .map(ProductDto::getName)
                .toList()).contains(INGREDIENTS);
    }
}