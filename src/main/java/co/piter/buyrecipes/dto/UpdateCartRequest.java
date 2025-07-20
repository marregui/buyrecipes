package co.piter.buyrecipes.dto;

import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNullElse;

public class UpdateCartRequest {

    private @Nullable Integer totalInCents;

    public UpdateCartRequest() {
        // used by JSON serdes
    }

    public UpdateCartRequest(final @Nullable Integer totalInCents) {
        this.totalInCents = requireNonNullElse(totalInCents, 0);
    }

    public @Nullable Integer getTotalInCents() {
        return requireNonNullElse(totalInCents, 0);
    }

    public void setTotalInCents(final @Nullable Integer totalInCents) {
        this.totalInCents = requireNonNullElse(totalInCents, 0);
    }

    @Override
    public @Nullable String toString() {
        return "UpdateCartRequest{totalInCents=" + totalInCents + '}';
    }
}