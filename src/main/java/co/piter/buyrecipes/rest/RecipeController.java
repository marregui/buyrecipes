package co.piter.buyrecipes.rest;

import co.piter.buyrecipes.dto.CreateRecipeRequest;
import co.piter.buyrecipes.dto.RecipeDto;
import co.piter.buyrecipes.dto.UpdateRecipeRequest;
import co.piter.buyrecipes.service.RecipeService;
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

@Controller("/recipes")
@Singleton
@Tag(name = "Recipes", description = "Recipe management")
public class RecipeController {

    private final @NotNull RecipeService recipeService;

    public RecipeController(final @NotNull RecipeService recipeService) {
        this.recipeService = requireNonNull(recipeService);
    }

    @Get("/test")
    public @NotNull HttpResponse<String> test() {
        return HttpResponse.ok("Controller is working!");
    }

    @Get
    @Operation(
            summary = "List all recipes",
            description = "Returns a list of all recipes in the system")
    @ApiResponse(
            responseCode = "200",
            description = "Successful operation",
            content = @Content(schema = @Schema(implementation = RecipeDto.class)))
    public @NotNull HttpResponse<List<RecipeDto>> getAllRecipes() {
        return HttpResponse.ok(recipeService.getAllRecipes());
    }

    @Get("/{id}")
    @Operation(
            summary = "Get a recipe by ID",
            description = "Returns a single recipe")
    @ApiResponse(
            responseCode = "200",
            description = "Recipe found",
            content = @Content(schema = @Schema(implementation = RecipeDto.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Recipe not found")
    public @NotNull HttpResponse<RecipeDto> getRecipeById(
            @Parameter(
                    description = "ID of recipe to return",
                    required = true) final @NotNull @PathVariable Long id) {
        return recipeService.getRecipeById(id)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Post
    @Operation(
            summary = "Create a new recipe",
            description = "Creates a new recipe in the system")
    @ApiResponse(
            responseCode = "200",
            description = "Recipe created successfully",
            content = @Content(schema = @Schema(implementation = RecipeDto.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input")
    public @NotNull HttpResponse<RecipeDto> createRecipe(
            @Parameter(
                    description = "Recipe to create",
                    required = true) final @NotNull @Body CreateRecipeRequest request) {
        return HttpResponse.ok(recipeService.createRecipe(request));
    }

    @Put("/{id}")
    @Operation(
            summary = "Update a recipe",
            description = "Updates an existing recipe")
    @ApiResponse(
            responseCode = "200",
            description = "Recipe updated successfully",
            content = @Content(schema = @Schema(implementation = RecipeDto.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Recipe not found")
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input")
    public @NotNull HttpResponse<RecipeDto> updateRecipe(
            @Parameter(
                    description = "ID of recipe to update",
                    required = true) final @NotNull @PathVariable Long id,
            @Parameter(
                    description = "Updated recipe information",
                    required = true) final @NotNull @Body UpdateRecipeRequest request) {
        return recipeService.updateRecipe(id, request)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Delete("/{id}")
    @Operation(
            summary = "Delete a recipe",
            description = "Deletes a recipe from the system")
    @ApiResponse(
            responseCode = "200",
            description = "Recipe deleted successfully")
    @ApiResponse(
            responseCode = "404",
            description = "Recipe not found")
    public @NotNull HttpResponse<Void> deleteRecipe(
            @Parameter(
                    description = "ID of recipe to delete",
                    required = true) final @NotNull @PathVariable Long id) {
        return recipeService.deleteRecipe(id) ? HttpResponse.ok() : HttpResponse.notFound();
    }
}