package co.piter.buyrecipes.rest;

import co.piter.buyrecipes.dto.CreateRecipeIngredientRequest;
import co.piter.buyrecipes.dto.RecipeIngredientDto;
import co.piter.buyrecipes.dto.UpdateRecipeIngredientRequest;
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
class RecipeIngredientControllerIT {

    @Inject
    @Client("/")
    @NotNull HttpClient client;

    @Test
    void testGetAllRecipeIngredients() {
        final HttpRequest<Object> request = HttpRequest.GET("/recipe-ingredients");
        final HttpResponse<List<RecipeIngredientDto>> response = client.toBlocking().exchange(request,
            Argument.listOf(RecipeIngredientDto.class));
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body()).isNotEmpty();
        
        final RecipeIngredientDto firstIngredient = response.body().get(0);
        Assertions.assertThat(firstIngredient.getId()).isNotNull();
        Assertions.assertThat(firstIngredient.getRecipeId()).isNotNull();
        Assertions.assertThat(firstIngredient.getProductId()).isNotNull();
        Assertions.assertThat(firstIngredient.getQuantity()).isNotNull();
        Assertions.assertThat(firstIngredient.getUnit()).isNotNull();
    }

    @Test
    void testCreateRecipeIngredient() {
        final CreateRecipeIngredientRequest request = new CreateRecipeIngredientRequest(1L, 1L, 3, "cups");
        final HttpRequest<CreateRecipeIngredientRequest> httpRequest = HttpRequest.POST("/recipe-ingredients", request);
        final HttpResponse<RecipeIngredientDto> response = client.toBlocking().exchange(httpRequest, RecipeIngredientDto.class);
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body().getId()).isNotNull();
        Assertions.assertThat(response.body().getRecipeId()).isEqualTo(1L);
        Assertions.assertThat(response.body().getProductId()).isEqualTo(1L);
        Assertions.assertThat(response.body().getQuantity()).isEqualTo(3);
        Assertions.assertThat(response.body().getUnit()).isEqualTo("cups");
    }

    @Test
    void testGetRecipeIngredientById() {
        final HttpRequest<Object> request = HttpRequest.GET("/recipe-ingredients/1");
        final HttpResponse<RecipeIngredientDto> response = client.toBlocking().exchange(request, RecipeIngredientDto.class);
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body().getId()).isEqualTo(1L);
        Assertions.assertThat(response.body().getRecipeId()).isNotNull();
        Assertions.assertThat(response.body().getProductId()).isNotNull();
        Assertions.assertThat(response.body().getQuantity()).isNotNull();
        Assertions.assertThat(response.body().getUnit()).isNotNull();
    }

    @Test
    void testGetRecipeIngredientByIdNotFound() {
        final HttpRequest<Object> request = HttpRequest.GET("/recipe-ingredients/999");
        
        Assertions.assertThatThrownBy(() -> client.toBlocking().exchange(request, RecipeIngredientDto.class))
            .isInstanceOf(HttpClientResponseException.class)
            .hasMessageContaining("Not Found");
    }

    @Test
    void testGetRecipeIngredientsByRecipeId() {
        final HttpRequest<Object> request = HttpRequest.GET("/recipe-ingredients/recipe/1");
        final HttpResponse<List<RecipeIngredientDto>> response = client.toBlocking().exchange(request,
            Argument.listOf(RecipeIngredientDto.class));
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body()).isNotEmpty();
        final RecipeIngredientDto firstIngredient = response.body().get(0);
        Assertions.assertThat(firstIngredient.getRecipeId()).isEqualTo(1L);
    }

    @Test
    void testGetRecipeIngredientsByProductId() {
        final HttpRequest<Object> request = HttpRequest.GET("/recipe-ingredients/product/1");
        final HttpResponse<List<RecipeIngredientDto>> response = client.toBlocking().exchange(request,
            Argument.listOf(RecipeIngredientDto.class));
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body()).isNotEmpty();
        final RecipeIngredientDto firstIngredient = response.body().get(0);
        Assertions.assertThat(firstIngredient.getProductId()).isEqualTo(1L);
    }

    @Test
    void testUpdateRecipeIngredient() {
        final UpdateRecipeIngredientRequest request = new UpdateRecipeIngredientRequest(1L, 2L, 5, "tablespoons");
        final HttpRequest<UpdateRecipeIngredientRequest> httpRequest = HttpRequest.PUT("/recipe-ingredients/1", request);
        final HttpResponse<RecipeIngredientDto> response = client.toBlocking().exchange(httpRequest, RecipeIngredientDto.class);
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body().getId()).isEqualTo(1L);
        Assertions.assertThat(response.body().getRecipeId()).isEqualTo(1L);
        Assertions.assertThat(response.body().getProductId()).isEqualTo(2L);
        Assertions.assertThat(response.body().getQuantity()).isEqualTo(5);
        Assertions.assertThat(response.body().getUnit()).isEqualTo("tablespoons");
    }

    @Test
    void testUpdateRecipeIngredientNotFound() {
        final UpdateRecipeIngredientRequest request = new UpdateRecipeIngredientRequest(1L, 2L, 5, "grams");
        final HttpRequest<UpdateRecipeIngredientRequest> httpRequest = HttpRequest.PUT("/recipe-ingredients/999", request);
        
        Assertions.assertThatThrownBy(() -> client.toBlocking().exchange(httpRequest, RecipeIngredientDto.class))
            .isInstanceOf(HttpClientResponseException.class)
            .hasMessageContaining("Not Found");
    }

    @Test
    void testDeleteRecipeIngredient() {
        final CreateRecipeIngredientRequest createRequest = new CreateRecipeIngredientRequest(2L, 3L, 2, "pieces");
        final HttpRequest<CreateRecipeIngredientRequest> createHttpRequest = HttpRequest.POST("/recipe-ingredients", createRequest);
        final HttpResponse<RecipeIngredientDto> createResponse = client.toBlocking().exchange(createHttpRequest, RecipeIngredientDto.class);
        final Long ingredientId = createResponse.body().getId();
        final HttpRequest<Object> deleteRequest = HttpRequest.DELETE("/recipe-ingredients/" + ingredientId);
        final HttpResponse<Void> response = client.toBlocking().exchange(deleteRequest, Void.class);
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
    }

    @Test
    void testDeleteRecipeIngredientNotFound() {
        final HttpRequest<Object> request = HttpRequest.DELETE("/recipe-ingredients/999");
        
        Assertions.assertThatThrownBy(() -> client.toBlocking().exchange(request, Void.class))
            .isInstanceOf(HttpClientResponseException.class)
            .hasMessageContaining("Not Found");
    }

    @Test
    void testCreateRecipeIngredientWithoutUnit() {
        final CreateRecipeIngredientRequest request = new CreateRecipeIngredientRequest(1L, 2L, 5, null);
        final HttpRequest<CreateRecipeIngredientRequest> httpRequest = HttpRequest.POST("/recipe-ingredients", request);
        final HttpResponse<RecipeIngredientDto> response = client.toBlocking().exchange(httpRequest, RecipeIngredientDto.class);
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body().getRecipeId()).isEqualTo(1L);
        Assertions.assertThat(response.body().getProductId()).isEqualTo(2L);
        Assertions.assertThat(response.body().getQuantity()).isEqualTo(5);
        Assertions.assertThat(response.body().getUnit()).isEqualTo("");
    }

    @Test
    void testUpdateRecipeIngredientWithEmptyUnit() {
        final UpdateRecipeIngredientRequest request = new UpdateRecipeIngredientRequest(1L, 1L, 10, "");
        final HttpRequest<UpdateRecipeIngredientRequest> httpRequest = HttpRequest.PUT("/recipe-ingredients/1", request);
        final HttpResponse<RecipeIngredientDto> response = client.toBlocking().exchange(httpRequest, RecipeIngredientDto.class);
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body().getQuantity()).isEqualTo(10);
        Assertions.assertThat(response.body().getUnit()).isEqualTo("");
    }
}