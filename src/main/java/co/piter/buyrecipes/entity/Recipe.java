package co.piter.buyrecipes.entity;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;


@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Nullable Long id; // PK auto-generated

    @Version
    private @Nullable Long version; // optimistic locking

    private @Nullable String name;
    private @Nullable String description;

    public Recipe() {
        // used by Hibernate serdes
    }

    public Recipe(
            final @NotNull String name,
            final @Nullable String description
    ) {
        this.name = requireNonNull(name);
        this.description = requireNonNullElse(description, "");
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
        return "Recipe{id=" + id + ", version=" + version + ", name='" + name + "', description='" + description + "'}";
    }
}