package co.piter.buyrecipes.rest;

import co.piter.buyrecipes.dto.AddRecipeRequest;
import co.piter.buyrecipes.dto.CartDto;
import co.piter.buyrecipes.dto.CreateCartRequest;
import co.piter.buyrecipes.dto.UpdateCartRequest;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

@MicronautTest
class CartControllerIT {

    @Inject
    @Client("/")
    @NotNull HttpClient client;

    @Test
    void testGetCartById() {
        final HttpRequest<Object> request = HttpRequest.GET("/carts/1");
        final HttpResponse<CartDto> response = client.toBlocking().exchange(request, CartDto.class);

        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body().getId()).isEqualTo(1L);
        Assertions.assertThat(response.body().getTotalInCents()).isNotNull();
        Assertions.assertThat(response.body().getItems()).isNotNull();
    }

    @Test
    void testGetCartByIdNotFound() {
        final HttpRequest<Object> request = HttpRequest.GET("/carts/999");

        Assertions.assertThatThrownBy(() -> client.toBlocking().exchange(request, CartDto.class))
                .isInstanceOf(HttpClientResponseException.class)
                .hasMessageContaining("Not Found");
    }

    @Test
    void testAddRecipeToCart() {
        final AddRecipeRequest addRecipeRequest = new AddRecipeRequest(1L);
        final HttpRequest<AddRecipeRequest> request = HttpRequest.POST("/carts/1/add_recipe", addRecipeRequest);
        final HttpResponse<CartDto> response = client.toBlocking().exchange(request, CartDto.class);

        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body().getId()).isEqualTo(1L);
        Assertions.assertThat(response.body().getItems()).isNotEmpty();
        Assertions.assertThat(response.body().getTotalInCents()).isGreaterThan(0);
    }

    @Test
    void testAddRecipeToCartNotFound() {
        final AddRecipeRequest addRecipeRequest = new AddRecipeRequest(999L);
        final HttpRequest<AddRecipeRequest> request = HttpRequest.POST("/carts/1/add_recipe", addRecipeRequest);

        Assertions.assertThatThrownBy(() -> client.toBlocking().exchange(request, CartDto.class))
                .isInstanceOf(HttpClientResponseException.class)
                .hasMessageContaining("Not Found");
    }

    @Test
    void testRemoveRecipeFromCart() {
        final AddRecipeRequest addRecipeRequest = new AddRecipeRequest(1L);
        final HttpRequest<AddRecipeRequest> addRequest = HttpRequest.POST("/carts/1/add_recipe", addRecipeRequest);
        client.toBlocking().exchange(addRequest, CartDto.class);
        final HttpRequest<Object> removeRequest = HttpRequest.DELETE("/carts/1/recipes/1");
        final HttpResponse<CartDto> response = client.toBlocking().exchange(removeRequest, CartDto.class);

        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body().getId()).isEqualTo(1L);
    }

    @Test
    void testRemoveRecipeFromCartNotFound() {
        final HttpRequest<Object> request = HttpRequest.DELETE("/carts/999/recipes/1");

        Assertions.assertThatThrownBy(() -> client.toBlocking().exchange(request, CartDto.class))
                .isInstanceOf(HttpClientResponseException.class)
                .hasMessageContaining("Not Found");
    }

    @Test
    void testGetAllCarts() {
        final HttpRequest<Object> request = HttpRequest.GET("/carts");
        final HttpResponse<List<CartDto>> response = client.toBlocking().exchange(request, Argument.listOf(CartDto.class));

        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body()).isNotEmpty();
        Assertions.assertThat(response.body().get(0).getId()).isNotNull();
        Assertions.assertThat(response.body().get(0).getTotalInCents()).isNotNull();
        Assertions.assertThat(response.body().get(0).getItems()).isNotNull();
    }

    @Test
    void testCreateCart() {
        final CreateCartRequest createRequest = new CreateCartRequest(500);
        final HttpRequest<CreateCartRequest> request = HttpRequest.POST("/carts", createRequest);
        final HttpResponse<CartDto> response = client.toBlocking().exchange(request, CartDto.class);

        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body().getId()).isNotNull();
        Assertions.assertThat(response.body().getTotalInCents()).isEqualTo(500);
        Assertions.assertThat(response.body().getItems()).isEmpty();
    }

    @Test
    void testUpdateCart() {
        final UpdateCartRequest updateRequest = new UpdateCartRequest(750);
        final HttpRequest<UpdateCartRequest> request = HttpRequest.PUT("/carts/1", updateRequest);
        final HttpResponse<CartDto> response = client.toBlocking().exchange(request, CartDto.class);

        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body().getId()).isEqualTo(1L);
        Assertions.assertThat(response.body().getTotalInCents()).isEqualTo(750);
        Assertions.assertThat(response.body().getItems()).isNotNull();
    }

    @Test
    void testUpdateCartNotFound() {
        final UpdateCartRequest updateRequest = new UpdateCartRequest(750);
        final HttpRequest<UpdateCartRequest> request = HttpRequest.PUT("/carts/999", updateRequest);

        Assertions.assertThatThrownBy(() -> client.toBlocking().exchange(request, CartDto.class))
                .isInstanceOf(HttpClientResponseException.class)
                .hasMessageContaining("Not Found");
    }

    @Test
    void testDeleteCart() {
        final CreateCartRequest createRequest = new CreateCartRequest(300);
        final HttpRequest<CreateCartRequest> createCartRequest = HttpRequest.POST("/carts", createRequest);
        final HttpResponse<CartDto> createResponse = client.toBlocking().exchange(createCartRequest, CartDto.class);
        final Long cartId = createResponse.body().getId();
        final HttpRequest<Object> deleteRequest = HttpRequest.DELETE("/carts/" + cartId);
        final HttpResponse<Void> response = client.toBlocking().exchange(deleteRequest, Void.class);

        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNull();
    }

    @Test
    void testDeleteCartNotFound() {
        final HttpRequest<Object> request = HttpRequest.DELETE("/carts/999");

        Assertions.assertThatThrownBy(() -> client.toBlocking().exchange(request, Void.class))
                .isInstanceOf(HttpClientResponseException.class)
                .hasMessageContaining("Not Found");
    }
}