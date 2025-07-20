package co.piter.buyrecipes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

@Schema(name = "CreateProductRequest", description = "Request to create a new product")
public class CreateProductRequest {

    @Schema(description = "Product name", example = "Chocolate Chips", required = true)
    private @Nullable String name;
    
    @Schema(description = "Price in cents", example = "399", minimum = "0", required = true)
    private @Nullable Integer priceInCents;

    public CreateProductRequest() {
        // used by JSON serdes
    }

    public CreateProductRequest(
            final @NotNull String name,
            final @Nullable Integer priceInCents) {
        this.name = requireNonNull(name);
        this.priceInCents = requireNonNullElse(priceInCents, 0);
    }

    public @NotNull String getName() {
        return requireNonNull(name);
    }

    public @NotNull Integer getPriceInCents() {
        return requireNonNullElse(priceInCents, 0);
    }

    public void setName(final @NotNull String name) {
        this.name = requireNonNull(name);
    }

    public void setPriceInCents(final @Nullable Integer priceInCents) {
        this.priceInCents = requireNonNullElse(priceInCents, 0);
    }
}