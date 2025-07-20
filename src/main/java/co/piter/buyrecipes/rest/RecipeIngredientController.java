package co.piter.buyrecipes.rest;

import co.piter.buyrecipes.dto.CreateRecipeIngredientRequest;
import co.piter.buyrecipes.dto.RecipeIngredientDto;
import co.piter.buyrecipes.dto.UpdateRecipeIngredientRequest;
import co.piter.buyrecipes.service.RecipeIngredientService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Controller("/recipe-ingredients")
@Singleton
@Tag(name = "Recipe Ingredients", description = "Recipe ingredient management")
public class RecipeIngredientController {

    private final @NotNull RecipeIngredientService recipeIngredientService;

    public RecipeIngredientController(final @NotNull RecipeIngredientService recipeIngredientService) {
        this.recipeIngredientService = requireNonNull(recipeIngredientService);
    }

    @Get
    @Operation(
            summary = "List all recipe ingredients",
            description = "Returns a list of all recipe ingredients")
    @ApiResponse(
            responseCode = "200",
            description = "Successful operation",
            content = @Content(schema = @Schema(implementation = RecipeIngredientDto.class)))
    public @NotNull HttpResponse<List<RecipeIngredientDto>> getAllRecipeIngredients() {
        return HttpResponse.ok(recipeIngredientService.getAllRecipeIngredients());
    }

    @Get("/{id}")
    @Operation(
            summary = "Get recipe ingredient by ID",
            description = "Returns a single recipe ingredient")
    @ApiResponse(
            responseCode = "200",
            description = "Recipe ingredient found",
            content = @Content(schema = @Schema(implementation = RecipeIngredientDto.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Recipe ingredient not found")
    public @NotNull HttpResponse<RecipeIngredientDto> getRecipeIngredientById(
            @Parameter(
                    description = "ID of recipe ingredient to return",
                    required = true) final @NotNull @PathVariable Long id) {
        return recipeIngredientService.getRecipeIngredientById(id)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Get("/recipe/{recipeId}")
    @Operation(
            summary = "Get ingredients for a recipe",
            description = "Returns all ingredients for a specific recipe")
    @ApiResponse(
            responseCode = "200",
            description = "Successful operation",
            content = @Content(schema = @Schema(implementation = RecipeIngredientDto.class)))
    public @NotNull HttpResponse<List<RecipeIngredientDto>> getRecipeIngredientsByRecipeId(
            @Parameter(
                    description = "ID of recipe to get ingredients for",
                    required = true) final @NotNull @PathVariable Long recipeId) {
        return HttpResponse.ok(recipeIngredientService.getRecipeIngredientsByRecipeId(recipeId));
    }

    @Get("/product/{productId}")
    @Operation(
            summary = "Get recipes using a product",
            description = "Returns all recipe ingredients that use a specific product")
    @ApiResponse(
            responseCode = "200",
            description = "Successful operation",
            content = @Content(schema = @Schema(implementation = RecipeIngredientDto.class)))
    public @NotNull HttpResponse<List<RecipeIngredientDto>> getRecipeIngredientsByProductId(
            @Parameter(
                    description = "ID of product to find recipe ingredients for",
                    required = true) final @NotNull @PathVariable Long productId) {
        return HttpResponse.ok(recipeIngredientService.getRecipeIngredientsByProductId(productId));
    }

    @Post
    @Operation(
            summary = "Create a recipe ingredient",
            description = "Adds a new ingredient to a recipe")
    @ApiResponse(
            responseCode = "200",
            description = "Recipe ingredient created successfully",
            content = @Content(schema = @Schema(implementation = RecipeIngredientDto.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input")
    public @NotNull HttpResponse<RecipeIngredientDto> createRecipeIngredient(
            @Parameter(
                    description = "Recipe ingredient to create",
                    required = true) final @NotNull @Body CreateRecipeIngredientRequest request) {
        return HttpResponse.ok(recipeIngredientService.createRecipeIngredient(request));
    }

    @Put("/{id}")
    @Operation(
            summary = "Update a recipe ingredient",
            description = "Updates an existing recipe ingredient")
    @ApiResponse(
            responseCode = "200",
            description = "Recipe ingredient updated successfully",
            content = @Content(schema = @Schema(implementation = RecipeIngredientDto.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Recipe ingredient not found")
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input")
    public @NotNull HttpResponse<RecipeIngredientDto> updateRecipeIngredient(
            @Parameter(
                    description = "ID of recipe ingredient to update",
                    required = true) final @NotNull @PathVariable Long id,
            @Parameter(
                    description = "Updated recipe ingredient information",
                    required = true) final @NotNull @Body UpdateRecipeIngredientRequest request) {
        return recipeIngredientService.updateRecipeIngredient(id, request)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Delete("/{id}")
    @Operation(
            summary = "Delete a recipe ingredient",
            description = "Removes an ingredient from a recipe")
    @ApiResponse(
            responseCode = "200",
            description = "Recipe ingredient deleted successfully")
    @ApiResponse(
            responseCode = "404",
            description = "Recipe ingredient not found")
    public @NotNull HttpResponse<Void> deleteRecipeIngredient(
            @Parameter(
                    description = "ID of recipe ingredient to delete",
                    required = true) final @NotNull @PathVariable Long id) {
        return recipeIngredientService.deleteRecipeIngredient(id) ? HttpResponse.ok() : HttpResponse.notFound();
    }
}