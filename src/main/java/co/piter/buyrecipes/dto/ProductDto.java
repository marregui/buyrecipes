package co.piter.buyrecipes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

@Schema(name = "Product", description = "Product information")
public class ProductDto {

    @Schema(description = "Unique identifier of the product", example = "1")
    private @Nullable Long id;
    
    @Schema(description = "Product name", example = "Flour", required = true)
    private @Nullable String name;
    
    @Schema(description = "Price in cents", example = "299", minimum = "0")
    private @Nullable Integer priceInCents;
    
    @Schema(description = "Quantity in stock", example = "100", minimum = "0")
    private @Nullable Integer quantity;
    // TODO: next feature after challenge -> add units here for inventory


    public ProductDto() {
        // used by JSON serdes
    }

    public ProductDto(
            final @NotNull Long id,
            final @NotNull String name,
            final @NotNull Integer priceInCents) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        this.priceInCents = requireNonNull(priceInCents);
    }

    public ProductDto(
            final @NotNull Long id,
            final @NotNull String name,
            final @NotNull Integer priceInCents,
            final @NotNull Integer quantity) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        this.priceInCents = requireNonNull(priceInCents);
        this.quantity = requireNonNull(quantity);
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

    public @NotNull Integer getPriceInCents() {
        return requireNonNullElse(priceInCents, 0);
    }

    public void setPriceInCents(final @Nullable Integer priceInCents) {
        this.priceInCents = requireNonNullElse(priceInCents, 0);
    }

    public @NotNull Integer getQuantity() {
        return requireNonNullElse(quantity, 0);
    }

    public void setQuantity(final @Nullable Integer quantity) {
        this.quantity = requireNonNullElse(quantity, 0);
    }

    @Override
    public final boolean equals(final @Nullable Object o) {
        return o instanceof ProductDto that &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(priceInCents, that.priceInCents) &&
                Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, priceInCents, quantity);
    }

    @Override
    public @NotNull String toString() {
        return "ProductDto{id=" + id + ", name='" + name + "', priceInCents=" + priceInCents + ", quantity=" + quantity + '}';
    }
}