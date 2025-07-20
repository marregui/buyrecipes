package co.piter.buyrecipes.dto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static co.piter.buyrecipes.entity.RecipeIngredient.DEFAULT_QUANTITY;
import static co.piter.buyrecipes.entity.RecipeIngredient.DEFAULT_UNIT;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

public class UpdateRecipeIngredientRequest {

    private @Nullable Long recipeId;
    private @Nullable Long productId;
    private @Nullable Integer quantity;
    private @Nullable String unit;

    public UpdateRecipeIngredientRequest() {
        // used by JSON serdes
    }

    public UpdateRecipeIngredientRequest(
            final @NotNull Long recipeId,
            final @NotNull Long productId,
            final @Nullable Integer quantity,
            final @Nullable String unit) {
        this.recipeId = requireNonNull(recipeId);
        this.productId = requireNonNull(productId);
        this.quantity = requireNonNullElse(quantity, DEFAULT_QUANTITY);
        this.unit = requireNonNullElse(unit, DEFAULT_UNIT);
    }

    public @NotNull Long getRecipeId() {
        return requireNonNull(recipeId);
    }

    public void setRecipeId(final @NotNull Long recipeId) {
        this.recipeId = requireNonNull(recipeId);
    }

    public @NotNull Long getProductId() {
        return requireNonNull(productId);
    }

    public void setProductId(final @NotNull Long productId) {
        this.productId = requireNonNull(productId);
    }

    public @NotNull Integer getQuantity() {
        return requireNonNullElse(quantity, DEFAULT_QUANTITY);
    }

    public void setQuantity(final @Nullable Integer quantity) {
        this.quantity = requireNonNullElse(quantity, DEFAULT_QUANTITY);
    }

    public @NotNull String getUnit() {
        return requireNonNullElse(unit, DEFAULT_UNIT);
    }

    public void setUnit(final @Nullable String unit) {
        this.unit = requireNonNullElse(unit, DEFAULT_UNIT);
    }

    @Override
    public @NotNull String toString() {
        return "UpdateRecipeIngredientRequest{recipeId=" + recipeId + ", productId=" + productId + ", quantity=" + quantity + ", unit='" + unit + "'}";
    }
}