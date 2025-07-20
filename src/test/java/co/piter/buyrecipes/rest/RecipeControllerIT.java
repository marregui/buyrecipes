package co.piter.buyrecipes.rest;

import co.piter.buyrecipes.dto.CreateRecipeRequest;
import co.piter.buyrecipes.dto.RecipeDto;
import co.piter.buyrecipes.dto.UpdateRecipeRequest;
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
class RecipeControllerIT {

    @Inject
    @Client("/")
    @NotNull HttpClient client;

    @Test
    void testGetAllRecipes() {
        final HttpRequest<Object> request = HttpRequest.GET("/recipes");
        HttpResponse<List<RecipeDto>> response = client.toBlocking().exchange(request, 
            Argument.listOf(RecipeDto.class));
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body()).isNotEmpty();
        
        RecipeDto firstRecipe = response.body().get(0);
        Assertions.assertThat(firstRecipe.getId()).isNotNull();
        Assertions.assertThat(firstRecipe.getName()).isNotNull();
        Assertions.assertThat(firstRecipe.getIngredients()).isNotNull();
        Assertions.assertThat(firstRecipe.getIngredients()).isNotEmpty();
    }

    @Test
    void testTestEndpoint() {
        final HttpRequest<Object> request = HttpRequest.GET("/recipes/test");
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isEqualTo("Controller is working!");
    }

    @Test
    void testCreateRecipe() {
        final CreateRecipeRequest request = new CreateRecipeRequest("Integration Test Recipe", "Test Description");
        final HttpRequest<CreateRecipeRequest> httpRequest = HttpRequest.POST("/recipes", request);
        final HttpResponse<RecipeDto> response = client.toBlocking().exchange(httpRequest, RecipeDto.class);
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body().getId()).isNotNull();
        Assertions.assertThat(response.body().getName()).isEqualTo("Integration Test Recipe");
        Assertions.assertThat(response.body().getDescription()).isEqualTo("Test Description");
    }

    @Test
    void testGetRecipeById() {
        final HttpRequest<Object> request = HttpRequest.GET("/recipes/1");
        final HttpResponse<RecipeDto> response = client.toBlocking().exchange(request, RecipeDto.class);
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body().getId()).isEqualTo(1L);
        Assertions.assertThat(response.body().getName()).isNotNull();
        Assertions.assertThat(response.body().getDescription()).isNotNull();
    }

    @Test
    void testGetRecipeByIdNotFound() {
        final HttpRequest<Object> request = HttpRequest.GET("/recipes/999");
        
        Assertions.assertThatThrownBy(() -> client.toBlocking().exchange(request, RecipeDto.class))
            .isInstanceOf(HttpClientResponseException.class)
            .hasMessageContaining("Not Found");
    }

    @Test
    void testUpdateRecipe() {
        final UpdateRecipeRequest request = new UpdateRecipeRequest("Updated Recipe", "Updated Description");
        final HttpRequest<UpdateRecipeRequest> httpRequest = HttpRequest.PUT("/recipes/1", request);
        final HttpResponse<RecipeDto> response = client.toBlocking().exchange(httpRequest, RecipeDto.class);
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isNotNull();
        Assertions.assertThat(response.body().getId()).isEqualTo(1L);
        Assertions.assertThat(response.body().getName()).isEqualTo("Updated Recipe");
        Assertions.assertThat(response.body().getDescription()).isEqualTo("Updated Description");
    }

    @Test
    void testUpdateRecipeNotFound() {
        final UpdateRecipeRequest request = new UpdateRecipeRequest("Updated Recipe", "Updated Description");
        final HttpRequest<UpdateRecipeRequest> httpRequest = HttpRequest.PUT("/recipes/999", request);
        
        Assertions.assertThatThrownBy(() -> client.toBlocking().exchange(httpRequest, RecipeDto.class))
            .isInstanceOf(HttpClientResponseException.class)
            .hasMessageContaining("Not Found");
    }

    @Test
    void testDeleteRecipe() {
        final CreateRecipeRequest createRequest = new CreateRecipeRequest("Recipe to Delete", "Test Description");
        final HttpRequest<CreateRecipeRequest> createHttpRequest = HttpRequest.POST("/recipes", createRequest);
        final HttpResponse<RecipeDto> createResponse = client.toBlocking().exchange(createHttpRequest, RecipeDto.class);
        final Long recipeId = createResponse.body().getId();
        
        final HttpRequest<Object> deleteRequest = HttpRequest.DELETE("/recipes/" + recipeId);
        final HttpResponse<Void> response = client.toBlocking().exchange(deleteRequest, Void.class);
        
        Assertions.assertThat(response.getStatus().getCode()).isEqualTo(200);
    }

    @Test
    void testDeleteRecipeNotFound() {
        final HttpRequest<Object> request = HttpRequest.DELETE("/recipes/999");
        
        Assertions.assertThatThrownBy(() -> client.toBlocking().exchange(request, Void.class))
            .isInstanceOf(HttpClientResponseException.class)
            .hasMessageContaining("Not Found");
    }
}