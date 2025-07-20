package co.piter.buyrecipes.entity;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Nullable Long id; // PK auto-generated

    @Version
    private @Nullable Long version; // optimistic locking

    private @Nullable String name;
    private @Nullable Integer priceInCents;

    public Product() {
        // used by Hibernate serdes
    }

    public Product(
            final @NotNull String name,
            final @Nullable Integer priceInCents) {
        this.name = requireNonNull(name);
        this.priceInCents = requireNonNullElse(priceInCents, 0);
    }

    public @Nullable Long getId() {
        return id;
    }

    public void setId(final @NotNull Long id) {
        this.id = id;
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

    public @NotNull Integer getPriceInCents() {
        return requireNonNullElse(priceInCents, 0);
    }

    public void setPriceInCents(final @Nullable Integer priceInCents) {
        this.priceInCents = requireNonNullElse(priceInCents, 0);
    }

    @Override
    public @NotNull String toString() {
        return "Product{id=" + id + ", version=" + version + ", name='" + name + "', priceInCents=" + priceInCents + '}';
    }
}