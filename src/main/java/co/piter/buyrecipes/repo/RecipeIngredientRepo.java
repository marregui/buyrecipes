package co.piter.buyrecipes.repo;

import co.piter.buyrecipes.entity.RecipeIngredient;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Repository
public interface RecipeIngredientRepo extends JpaRepository<RecipeIngredient, Long> {
    // JpaRepository provides:
    // - save(Cart cart): create/update a cart
    // - findById(Long id): find a cart by ID
    // - findAll(): get all carts
    // - deleteById(Long id): delete a cart by ID
    // - existsById(Long id): check if a cart exists by ID

    @Nullable List<RecipeIngredient> findByRecipeId(final @NotNull Long recipeId);

    @Nullable List<RecipeIngredient> findByProductId(final @NotNull Long productId);

    long deleteByRecipeId(final @NotNull Long recipeId);
}