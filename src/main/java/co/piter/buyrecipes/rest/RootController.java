package co.piter.buyrecipes.rest;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

@Controller
@Singleton
public class RootController {

    @Get()
    @Hidden
    public @NotNull HttpResponse<?> root() {
        return HttpResponse.redirect(URI.create("/swagger-ui"));
    }
}