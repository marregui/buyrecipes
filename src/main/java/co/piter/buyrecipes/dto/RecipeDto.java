package co.piter.buyrecipes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

@Schema(name = "Recipe", description = "Recipe information")
public class RecipeDto {

    @Schema(description = "Unique identifier of the recipe", example = "1")
    private @Nullable Long id;
    
    @Schema(description = "Recipe name", example = "Chocolate Chip Cookies", required = true)
    private @Nullable String name;
    
    @Schema(description = "Recipe description", example = "Delicious homemade chocolate chip cookies")
    private @Nullable String description;
    
    @Schema(description = "List of ingredients used in this recipe")
    private @Nullable List<ProductDto> ingredients;

    public RecipeDto() {
        // used by JSON serdes
    }

    public RecipeDto(
            final @NotNull Long id,
            final @NotNull String name,
            final @Nullable String description,
            final @NotNull List<ProductDto> ingredients) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        this.description = requireNonNullElse(description, "");
        this.ingredients = requireNonNull(ingredients);
    }

    public @NotNull Long getId() {
        return requireNonNull(id);
    }

    public void setId(final @NotNull Long id) {
        this.id = requireNonNull(id);
    }

    public @NotNull String getName() {
        return requireNonNull(name);
    }

    public void setName(final @NotNull String name) {
        this.name = requireNonNull(name);
    }

    public @NotNull String getDescription() {
        return requireNonNullElse(description, "");
    }

    public void setDescription(final @Nullable String description) {
        this.description = requireNonNullElse(description, "");
    }

    public @NotNull List<ProductDto> getIngredients() {
        return requireNonNullElse(ingredients, List.of());
    }

    public void setIngredients(final @Nullable List<ProductDto> ingredients) {
        this.ingredients = requireNonNullElse(ingredients, List.of());
    }

    @Override
    public final boolean equals(final @Nullable Object o) {
        return o instanceof RecipeDto that &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(ingredients, that.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, ingredients);
    }

    @Override
    public @NotNull String toString() {
        return "RecipeDto{id=" + id + ", name='" + name + "', description='" + description + "', ingredients=" + ingredients + '}';
    }
}