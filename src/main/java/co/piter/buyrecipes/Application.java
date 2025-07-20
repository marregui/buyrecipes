package co.piter.buyrecipes;

import io.micronaut.runtime.Micronaut;
import org.jetbrains.annotations.NotNull;

public class Application {
    public static void main(final @NotNull String @NotNull [] args) {
        Micronaut.run(Application.class, args);
    }
}