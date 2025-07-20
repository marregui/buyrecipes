package co.piter.buyrecipes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static co.piter.buyrecipes.entity.RecipeIngredient.DEFAULT_QUANTITY;
import static co.piter.buyrecipes.entity.RecipeIngredient.DEFAULT_UNIT;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

@Schema(name = "CreateRecipeIngredientRequest", description = "Request to add an ingredient to a recipe")
public class CreateRecipeIngredientRequest {

    @Schema(description = "ID of the recipe", example = "1", required = true)
    private @Nullable Long recipeId;
    
    @Schema(description = "ID of the product to add as ingredient", example = "1", required = true)
    private @Nullable Long productId;
    
    @Schema(description = "Quantity needed", example = "2", minimum = "1")
    private @Nullable Integer quantity;
    
    @Schema(description = "Unit of measurement", example = "cups")
    private @Nullable String unit;

    public CreateRecipeIngredientRequest() {
        // used by JSON serdes
    }

    public CreateRecipeIngredientRequest(
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
        return "CreateRecipeIngredientRequest{recipeId=" + recipeId + ", productId=" + productId + ", quantity=" + quantity + ", unit='" + unit + "'}";
    }
}