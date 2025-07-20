package co.piter.buyrecipes;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "Buy Recipes API",
        version = "1.0",
        description = "REST API for managing recipes, products, and shopping carts." +
                     "This is a public API with no authentication required.",
        contact = @Contact(
            name = "Buy Recipes Team",
            email = "miguel.arregui@gmail.com"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Local development server")
    }
)
public class OpenApiConfig {
    // OpenAPI configuration
}