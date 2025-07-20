package co.piter.buyrecipes.entity;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Nullable Long id; // PK auto-generated

    @Version
    private @Nullable Long version; // lock optimisation

    private @Nullable Integer totalInCents;

    public Cart() {
        // used by Hibernate serdes
    }

    public Cart(final @Nullable Integer totalInCents) {
        this.totalInCents = requireNonNullElse(totalInCents, 0);
    }

    public @Nullable Long getId() {
        return id;
    }

    public void setId(final @NotNull Long id) {
        this.id = requireNonNull(id);
    }

    public @NotNull Integer getTotalInCents() {
        return requireNonNullElse(totalInCents, 0);
    }

    public void setTotalInCents(final @Nullable Integer totalInCents) {
        this.totalInCents = requireNonNullElse(totalInCents, 0);
    }

    public @Nullable Long getVersion() {
        return version;
    }

    public void setVersion(final @Nullable Long version) {
        this.version = version;
    }

    @Override
    public @NotNull String toString() {
        return "Cart{id=" + id + ", version=" + version + ", totalInCents=" + totalInCents + '}';
    }
}