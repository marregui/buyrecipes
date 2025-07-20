package co.piter.buyrecipes.service;

import co.piter.buyrecipes.dto.CreateRecipeRequest;
import co.piter.buyrecipes.dto.ProductDto;
import co.piter.buyrecipes.dto.RecipeDto;
import co.piter.buyrecipes.dto.UpdateRecipeRequest;
import co.piter.buyrecipes.entity.Recipe;
import co.piter.buyrecipes.entity.RecipeIngredient;
import co.piter.buyrecipes.repo.ProductRepo;
import co.piter.buyrecipes.repo.RecipeIngredientRepo;
import co.piter.buyrecipes.repo.RecipeRepo;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Singleton
public class RecipeService {

    private final @NotNull RecipeRepo recipeRepo;
    private final @NotNull RecipeIngredientRepo recipeIngredientRepo;
    private final @NotNull ProductRepo productRepo;

    public RecipeService(
            final @NotNull RecipeRepo recipeRepo,
            final @NotNull RecipeIngredientRepo recipeIngredientRepo,
            final @NotNull ProductRepo productRepo
    ) {
        this.recipeRepo = requireNonNull(recipeRepo);
        this.recipeIngredientRepo = requireNonNull(recipeIngredientRepo);
        this.productRepo = requireNonNull(productRepo);
    }

    public @NotNull List<RecipeDto> getAllRecipes() {
        return Optional.ofNullable(recipeRepo.findAll())
                .orElse(List.of())
                .stream()
                .map(this::toDto)
                .toList();
    }

    public @NotNull Optional<RecipeDto> getRecipeById(final @NotNull Long recipeId) {
        return recipeRepo.findById(recipeId).map(this::toDto);
    }

    @Transactional
    public @NotNull RecipeDto createRecipe(final @NotNull CreateRecipeRequest request) {
        return toDto(recipeRepo.save(new Recipe(request.getName(), request.getDescription())));
    }

    @Transactional
    public @NotNull Optional<RecipeDto> updateRecipe(final @NotNull Long recipeId, final @NotNull UpdateRecipeRequest request) {
        return recipeRepo.findById(recipeId).map(recipe -> {
            recipe.setName(request.getName());
            recipe.setDescription(request.getDescription());
            return toDto(recipeRepo.save(recipe));
        });
    }

    @Transactional
    public boolean deleteRecipe(final @NotNull Long recipeId) {
        return recipeRepo.findById(recipeId).map(recipe -> {
            recipeIngredientRepo.deleteByRecipeId(recipeId);
            recipeRepo.delete(recipe);
            return true;
        }).orElse(false);
    }

    public @NotNull List<ProductDto> getRecipeIngredients(final @NotNull Long recipeId) {
        return Optional.ofNullable(recipeIngredientRepo.findByRecipeId(recipeId))
                .orElse(List.of())
                .stream()
                .map(this::toDto)
                .toList();
    }

    private @NotNull RecipeDto toDto(final @NotNull Recipe recipe) {
        return new RecipeDto(
                requireNonNull(recipe.getId()),
                recipe.getName(),
                recipe.getDescription(),
                getRecipeIngredients(recipe.getId()));
    }

    private @NotNull ProductDto toDto(final @NotNull RecipeIngredient recipeIngredient) {
        return productRepo.findById(recipeIngredient.getProductId())
                .map(product -> new ProductDto(
                        requireNonNull(product.getId()),
                        product.getName(),
                        product.getPriceInCents(),
                        recipeIngredient.getQuantity()))
                .orElseThrow(() -> new RuntimeException("Product not found for ingredient: " + recipeIngredient.getProductId()));
    }
}