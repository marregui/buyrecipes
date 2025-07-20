package co.piter.buyrecipes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

@Schema(name = "UpdateProductRequest", description = "Request to update an existing product")
public class UpdateProductRequest {

    @Schema(description = "Product name", example = "Dark Chocolate Chips", required = true)
    private @Nullable String name;
    
    @Schema(description = "Price in cents", example = "450", minimum = "0", required = true)
    private @Nullable Integer priceInCents;

    public UpdateProductRequest() {
        // used by JSON serdes
    }

    public UpdateProductRequest(final @NotNull String name, final @Nullable Integer priceInCents) {
        this.name = requireNonNull(name);
        this.priceInCents = requireNonNullElse(priceInCents, 0);
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
}