package co.piter.buyrecipes.rest;

import co.piter.buyrecipes.dto.CreateProductRequest;
import co.piter.buyrecipes.dto.ProductDto;
import co.piter.buyrecipes.dto.UpdateProductRequest;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import org.assertj.core.api.Assertions;

@MicronautTest
class ProductControllerIT {

    @Inject
    @Client("/")
    @NotNull HttpClient client;

    @Test
    void testCreateProduct() {
        final CreateProductRequest request = new CreateProductRequest("Integration Test Product", 500);
        final HttpRequest<CreateProductRequest> httpRequest = HttpRequest.POST("/products", request);
        final HttpResponse<ProductDto> response = client.toBlocking().exchange(httpRequest, ProductDto.class);
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body().getId()).isNotNull();
        Assertions.assertThat(response.body().getName()).isEqualTo("Integration Test Product");
        Assertions.assertThat(response.body().getPriceInCents()).isEqualTo(500);
    }

    @Test
    void testGetAllProducts() {
        final HttpRequest<Object> request = HttpRequest.GET("/products");
        final HttpResponse<List<ProductDto>> response = client.toBlocking().exchange(request,
            Argument.listOf(ProductDto.class));
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body()).isNotEmpty();

        final ProductDto firstProduct = response.body().get(0);
        Assertions.assertThat(firstProduct.getId()).isNotNull();
        Assertions.assertThat(firstProduct.getName()).isNotNull();
        Assertions.assertThat(firstProduct.getPriceInCents()).isNotNull();
    }

    @Test
    void testGetProductById() {
        final HttpRequest<Object> request = HttpRequest.GET("/products/1");
        final HttpResponse<ProductDto> response = client.toBlocking().exchange(request, ProductDto.class);
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body().getId()).isEqualTo(1L);
        Assertions.assertThat(response.body().getName()).isNotNull();
        Assertions.assertThat(response.body().getPriceInCents()).isNotNull();
    }

    @Test
    void testGetProductByIdNotFound() {
        final HttpRequest<Object> request = HttpRequest.GET("/products/999");
        
        Assertions.assertThatThrownBy(() -> client.toBlocking().exchange(request, ProductDto.class))
            .isInstanceOf(HttpClientResponseException.class)
            .hasMessageContaining("Not Found");
    }

    @Test
    void testUpdateProduct() {
        final UpdateProductRequest request = new UpdateProductRequest("Updated Product", 1000);
        final HttpRequest<UpdateProductRequest> httpRequest = HttpRequest.PUT("/products/1", request);
        final HttpResponse<ProductDto> response = client.toBlocking().exchange(httpRequest, ProductDto.class);
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body().getId()).isEqualTo(1L);
        Assertions.assertThat(response.body().getName()).isEqualTo("Updated Product");
        Assertions.assertThat(response.body().getPriceInCents()).isEqualTo(1000);
    }

    @Test
    void testUpdateProductNotFound() {
        final UpdateProductRequest request = new UpdateProductRequest("Updated Product", 1000);
        final HttpRequest<UpdateProductRequest> httpRequest = HttpRequest.PUT("/products/999", request);
        
        Assertions.assertThatThrownBy(() -> client.toBlocking().exchange(httpRequest, ProductDto.class))
            .isInstanceOf(HttpClientResponseException.class)
            .hasMessageContaining("Not Found");
    }

    @Test
    void testDeleteProduct() {
        final CreateProductRequest createRequest = new CreateProductRequest("Product to Delete", 100);
        final HttpRequest<CreateProductRequest> createHttpRequest = HttpRequest.POST("/products", createRequest);
        final HttpResponse<ProductDto> createResponse = client.toBlocking().exchange(createHttpRequest, ProductDto.class);
        final Long productId = createResponse.body().getId();
        final HttpRequest<Object> deleteRequest = HttpRequest.DELETE("/products/" + productId);
        final HttpResponse<Void> response = client.toBlocking().exchange(deleteRequest, Void.class);
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
    }

    @Test
    void testDeleteProductNotFound() {
        final HttpRequest<Object> request = HttpRequest.DELETE("/products/999");
        
        Assertions.assertThatThrownBy(() -> client.toBlocking().exchange(request, Void.class))
            .isInstanceOf(HttpClientResponseException.class)
            .hasMessageContaining("Not Found");
    }
}