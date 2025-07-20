package co.piter.buyrecipes.entity;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Nullable Long id; // PK auto-generated

    private @Nullable Long cartId; // FK to cart
    private @Nullable Long productId; // FK to product

    public CartItem() {
        // used by Hibernate serdes
    }

    public CartItem(
            final @NotNull Long cartId,
            final @NotNull Long productId
    ) {
        this.cartId = requireNonNull(cartId);
        this.productId = requireNonNull(productId);
    }

    public @Nullable Long getId() {
        return id;
    }

    public void setId(final @NotNull Long id) {
        this.id = requireNonNull(id);
    }

    public @NotNull Long getCartId() {
        return requireNonNull(cartId);
    }

    public void setCartId(final @NotNull Long cartId) {
        this.cartId = requireNonNull(cartId);
    }

    public @NotNull Long getProductId() {
        return requireNonNull(productId);
    }

    public void setProductId(final @NotNull Long productId) {
        this.productId = requireNonNull(productId);
    }

    @Override
    public @NotNull String toString() {
        return "CartItem{id=" + id + ", cartId=" + cartId + ", productId=" + productId + '}';
    }
}