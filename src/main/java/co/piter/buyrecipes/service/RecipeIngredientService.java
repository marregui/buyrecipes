package co.piter.buyrecipes.service;

import co.piter.buyrecipes.dto.CreateRecipeIngredientRequest;
import co.piter.buyrecipes.dto.RecipeIngredientDto;
import co.piter.buyrecipes.dto.UpdateRecipeIngredientRequest;
import co.piter.buyrecipes.entity.Product;
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
public class RecipeIngredientService {

    private final @NotNull RecipeIngredientRepo recipeIngredientRepo;
    private final @NotNull RecipeRepo recipeRepo;
    private final @NotNull ProductRepo productRepo;

    public RecipeIngredientService(
            final @NotNull RecipeIngredientRepo recipeIngredientRepo,
            final @NotNull RecipeRepo recipeRepo,
            final @NotNull ProductRepo productRepo) {
        this.recipeIngredientRepo = requireNonNull(recipeIngredientRepo);
        this.recipeRepo = requireNonNull(recipeRepo);
        this.productRepo = requireNonNull(productRepo);
    }

    public @NotNull List<RecipeIngredientDto> getAllRecipeIngredients() {
        return Optional.ofNullable(recipeIngredientRepo.findAll())
                .orElse(List.of())
                .stream()
                .map(this::toDto)
                .toList();
    }

    public @NotNull Optional<RecipeIngredientDto> getRecipeIngredientById(final @NotNull Long recipeIngredientId) {
        return recipeIngredientRepo.findById(recipeIngredientId).map(this::toDto);
    }

    public @NotNull List<RecipeIngredientDto> getRecipeIngredientsByRecipeId(final @NotNull Long recipeId) {
        return Optional.ofNullable(recipeIngredientRepo.findByRecipeId(recipeId))
                .orElse(List.of())
                .stream()
                .map(this::toDto)
                .toList();
    }

    public @NotNull List<RecipeIngredientDto> getRecipeIngredientsByProductId(final @NotNull Long productId) {
        return Optional.ofNullable(recipeIngredientRepo.findByProductId(productId))
                .orElse(List.of())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public @NotNull RecipeIngredientDto createRecipeIngredient(final @NotNull CreateRecipeIngredientRequest request) {
        if (recipeRepo.findById(request.getRecipeId()).isEmpty()) {
            throw new RuntimeException("Recipe not found with id: " + request.getRecipeId());
        }
        if (productRepo.findById(request.getProductId()).isEmpty()) {
            throw new RuntimeException("Product not found with id: " + request.getProductId());
        }
        return toDto(recipeIngredientRepo.save(
                new RecipeIngredient(
                        request.getRecipeId(),
                        request.getProductId(),
                        request.getQuantity(),
                        request.getUnit())));
    }

    @Transactional
    public @NotNull Optional<RecipeIngredientDto> updateRecipeIngredient(
            final @NotNull Long recipeIngredientId,
            final @NotNull UpdateRecipeIngredientRequest request) {
        return recipeIngredientRepo.findById(recipeIngredientId)
                .map(ingredient -> {
                    if (recipeRepo.findById(request.getRecipeId()).isEmpty()) {
                        throw new RuntimeException("Recipe not found with id: " + request.getRecipeId());
                    }
                    if (productRepo.findById(request.getProductId()).isEmpty()) {
                        throw new RuntimeException("Product not found with id: " + request.getProductId());
                    }
                    ingredient.setRecipeId(request.getRecipeId());
                    ingredient.setProductId(request.getProductId());
                    ingredient.setQuantity(request.getQuantity());
                    ingredient.setUnit(request.getUnit());
                    return toDto(recipeIngredientRepo.save(ingredient));
                });
    }

    @Transactional
    public boolean deleteRecipeIngredient(final @NotNull Long recipeIngredientId) {
        return recipeIngredientRepo.findById(recipeIngredientId).map(ingredient -> {
            recipeIngredientRepo.delete(ingredient);
            return true;
        }).orElse(false);
    }

    private @NotNull RecipeIngredientDto toDto(final @NotNull RecipeIngredient recipeIngredient) {
        return new RecipeIngredientDto(
                requireNonNull(recipeIngredient.getId()),
                recipeIngredient.getRecipeId(),
                recipeIngredient.getProductId(),
                productRepo.findById(recipeIngredient.getProductId())
                        .map(Product::getName)
                        .orElse(""),
                recipeIngredient.getQuantity(),
                recipeIngredient.getUnit());
    }
}