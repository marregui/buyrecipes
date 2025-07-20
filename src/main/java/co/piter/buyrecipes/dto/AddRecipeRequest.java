package co.piter.buyrecipes.dto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class AddRecipeRequest {

    private @Nullable Long recipeId;

    public AddRecipeRequest() {
        // used by JSON serdes
    }

    public AddRecipeRequest(final @NotNull Long recipeId) {
        this.recipeId = requireNonNull(recipeId);
    }

    public @NotNull Long getRecipeId() {
        return requireNonNull(recipeId);
    }

    public void setRecipeId(final @NotNull Long recipeId) {
        this.recipeId = requireNonNull(recipeId);
    }

    @Override
    public final boolean equals(final @Nullable Object o) {
        return o instanceof AddRecipeRequest that && Objects.equals(recipeId, that.recipeId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(recipeId);
    }

    @Override
    public @NotNull String toString() {
        return "AddRecipeRequest{recipeId=" + recipeId + '}';
    }
}