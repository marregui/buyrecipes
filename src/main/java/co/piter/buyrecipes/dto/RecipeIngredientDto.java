package co.piter.buyrecipes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static co.piter.buyrecipes.entity.RecipeIngredient.DEFAULT_QUANTITY;
import static co.piter.buyrecipes.entity.RecipeIngredient.DEFAULT_UNIT;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

@Schema(name = "RecipeIngredient", description = "Recipe ingredient information")
public class RecipeIngredientDto {

    @Schema(description = "Unique identifier of the recipe ingredient", example = "1")
    private @Nullable Long id;
    
    @Schema(description = "ID of the recipe this ingredient belongs to", example = "1")
    private @Nullable Long recipeId;
    
    @Schema(description = "ID of the product used as ingredient", example = "1")
    private @Nullable Long productId;
    
    @Schema(description = "Name of the product", example = "Flour")
    private @Nullable String productName;
    
    @Schema(description = "Quantity needed", example = "2", minimum = "1")
    private @Nullable Integer quantity;
    
    @Schema(description = "Unit of measurement", example = "cups")
    private @Nullable String unit;

    public RecipeIngredientDto() {
        // used by JSON serdes
    }

    public RecipeIngredientDto(
            final @NotNull Long id,
            final @NotNull Long recipeId,
            final @NotNull Long productId,
            final @Nullable String productName,
            final @Nullable Integer quantity,
            final @Nullable String unit) {
        this.id = requireNonNull(id);
        this.recipeId = requireNonNull(recipeId);
        this.productId = requireNonNull(productId);
        this.productName = productName;
        this.quantity = requireNonNullElse(quantity, 1);
        this.unit = requireNonNullElse(unit, DEFAULT_UNIT);
    }

    public @NotNull Long getId() {
        return requireNonNull(id);
    }

    public void setId(final @NotNull Long id) {
        this.id = requireNonNull(id);
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

    public @NotNull String getProductName() {
        return requireNonNullElse(productName, "");
    }

    public void setProductName(final @Nullable String productName) {
        this.productName = productName;
    }

    public @NotNull String getUnit() {
        return requireNonNullElse(unit, DEFAULT_UNIT);
    }

    public void setUnit(final @Nullable String unit) {
        this.unit = requireNonNullElse(unit, DEFAULT_UNIT);
    }

    @Override
    public final boolean equals(final @Nullable Object o) {
        return o instanceof RecipeIngredientDto that &&
                Objects.equals(id, that.id) &&
                Objects.equals(recipeId, that.recipeId) &&
                Objects.equals(productId, that.productId) &&
                Objects.equals(productName, that.productName) &&
                Objects.equals(quantity, that.quantity) &&
                Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, recipeId, productId, productName, quantity, unit);
    }

    @Override
    public @NotNull String toString() {
        return "RecipeIngredientDto{id=" + id + ", recipeId=" + recipeId + ", productId=" + productId + ", productName='" + productName + "', quantity=" + quantity + ", unit='" + unit + "'}";
    }
}