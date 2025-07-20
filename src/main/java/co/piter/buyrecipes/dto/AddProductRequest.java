package co.piter.buyrecipes.dto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class AddProductRequest {

    private @Nullable Long productId;

    public AddProductRequest() {
        // used by JSON serdes
    }

    public AddProductRequest(final @NotNull Long productId) {
        this.productId = requireNonNull(productId);
    }

    public @NotNull Long getProductId() {
        return requireNonNull(productId);
    }

    public void setProductId(final @NotNull Long productId) {
        this.productId = requireNonNull(productId);
    }

    @Override
    public final boolean equals(final @Nullable Object o) {
        return o instanceof AddProductRequest that && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(productId);
    }

    @Override
    public @NotNull String toString() {
        return "AddProductRequest{productId=" + productId + '}';
    }
}