package co.piter.buyrecipes.entity;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

@Entity
@Table(name = "recipe_ingredients")
public class RecipeIngredient {

    public static final @NotNull Integer DEFAULT_QUANTITY = 1;
    public static final @NotNull String DEFAULT_UNIT = "";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Nullable Long id; // PK auto-generated

    @Version
    private @Nullable Long version; // optimistic locking

    private @Nullable Long recipeId; // FK to recipe
    private @Nullable Long productId; // FK to product
    private @Nullable Integer quantity;

    @Column(length = 50)
    private @Nullable String unit;

    public RecipeIngredient() {
        // used by Hibernate serdes
    }

    public RecipeIngredient(
            final @NotNull Long recipeId,
            final @NotNull Long productId,
            final @Nullable Integer quantity) {
        this(recipeId, productId, quantity, DEFAULT_UNIT);
    }

    public RecipeIngredient(
            final @NotNull Long recipeId,
            final @NotNull Long productId,
            final @Nullable Integer quantity,
            final @Nullable String unit) {
        this.recipeId = requireNonNull(recipeId);
        this.productId = requireNonNull(productId);
        this.quantity = requireNonNullElse(quantity, DEFAULT_QUANTITY);
        this.unit = requireNonNullElse(unit, DEFAULT_UNIT);
    }

    public @Nullable Long getId() {
        return id;
    }

    public void setId(final @NotNull Long id) {
        this.id = requireNonNull(id);
    }

    public @Nullable Long getVersion() {
        return version;
    }

    public void setVersion(final @Nullable Long version) {
        this.version = version;
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
        return "RecipeIngredient{id=" + id + ", version=" + version + ", recipeId=" + recipeId + ", productId=" + productId + ", quantity=" + quantity + ", unit='" + unit + "'}";
    }
}