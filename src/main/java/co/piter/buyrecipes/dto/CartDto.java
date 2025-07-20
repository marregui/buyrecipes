package co.piter.buyrecipes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

@Schema(name = "Cart", description = "Shopping cart information")
public class CartDto {

    @Schema(description = "Unique identifier of the cart", example = "1")
    private @Nullable Long id;
    
    @Schema(description = "Total cost in cents", example = "1299", minimum = "0")
    private @Nullable Integer totalInCents;
    
    @Schema(description = "Products currently in this cart")
    private @Nullable List<ProductDto> items;

    public CartDto() {
        // used by JSON serdes
    }

    public CartDto(
            final @NotNull Long id,
            final @Nullable Integer totalInCents,
            final @NotNull List<ProductDto> items) {
        this.id = requireNonNull(id);
        this.totalInCents = requireNonNullElse(totalInCents, 0);
        this.items = requireNonNull(items);
    }

    public @NotNull Long getId() {
        return requireNonNull(id);
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

    public final @NotNull List<ProductDto> getItems() {
        return requireNonNullElse(items, List.of());
    }

    public void setItems(final @NotNull List<ProductDto> items) {
        this.items = requireNonNullElse(items, List.of());
    }

    @Override
    public final boolean equals(final @Nullable Object o) {
        return o instanceof CartDto that &&
                Objects.equals(id, that.id) &&
                Objects.equals(totalInCents, that.totalInCents) &&
                Objects.equals(items, that.items);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, totalInCents, items);
    }

    @Override
    public @NotNull String toString() {
        return "CartDto{id=" + id + ", totalInCents=" + totalInCents + ", items=" + items + '}';
    }
}