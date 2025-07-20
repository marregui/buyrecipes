package co.piter.buyrecipes.dto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

public class UpdateRecipeRequest {

    private @Nullable String name;
    private @Nullable String description;

    public UpdateRecipeRequest() {
        // used by JSON serdes
    }

    public UpdateRecipeRequest(final @NotNull String name, final @Nullable String description) {
        this.name = requireNonNull(name);
        this.description = requireNonNullElse(description, "");
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

    @Override
    public @NotNull String toString() {
        return "UpdateRecipeRequest{name='" + name + "', description='" + description + "'}";
    }
}