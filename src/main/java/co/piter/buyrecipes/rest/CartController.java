package co.piter.buyrecipes.rest;

import co.piter.buyrecipes.dto.AddProductRequest;
import co.piter.buyrecipes.dto.AddRecipeRequest;
import co.piter.buyrecipes.dto.CartDto;
import co.piter.buyrecipes.dto.CreateCartRequest;
import co.piter.buyrecipes.dto.UpdateCartRequest;
import co.piter.buyrecipes.service.CartService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Controller("/carts")
@Singleton
@Tag(name = "Shopping Carts", description = "Shopping cart management")
public class CartController {

    private final @NotNull CartService cartService;

    public CartController(final @NotNull CartService cartService) {
        this.cartService = requireNonNull(cartService);
    }

    @Get
    @Operation(
            summary = "List all carts",
            description = "Returns a list of all shopping carts")
    @ApiResponse(
            responseCode = "200",
            description = "Successful operation",
            content = @Content(schema = @Schema(implementation = CartDto.class)))
    public @NotNull HttpResponse<List<CartDto>> getAllCarts() {
        return HttpResponse.ok(cartService.getAllCarts());
    }

    @Post
    @Operation(
            summary = "Create a new cart",
            description = "Creates a new shopping cart")
    @ApiResponse(
            responseCode = "200",
            description = "Cart created successfully",
            content = @Content(schema = @Schema(implementation = CartDto.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input")
    public @NotNull HttpResponse<CartDto> createCart(
            @Parameter(
                    description = "Cart to create",
                    required = true) final @NotNull @Body CreateCartRequest request) {
        return HttpResponse.ok(cartService.createCart(request));
    }

    @Put("/{cartId}")
    @Operation(
            summary = "Update a cart",
            description = "Updates an existing shopping cart")
    @ApiResponse(
            responseCode = "200",
            description = "Cart updated successfully",
            content = @Content(schema = @Schema(implementation = CartDto.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Cart not found")
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input")
    public @NotNull HttpResponse<CartDto> updateCart(
            @Parameter(
                    description = "ID of cart to update",
                    required = true) final @NotNull @PathVariable Long cartId,
            @Parameter(
                    description = "Updated cart information",
                    required = true) final @NotNull @Body UpdateCartRequest request
    ) {
        return cartService.updateCart(cartId, request)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Delete("/{cartId}")
    @Operation(
            summary = "Delete a cart",
            description = "Deletes a shopping cart")
    @ApiResponse(
            responseCode = "200",
            description = "Cart deleted successfully")
    @ApiResponse(
            responseCode = "404",
            description = "Cart not found")
    public @NotNull HttpResponse<Void> deleteCart(
            @Parameter(
                    description = "ID of cart to delete",
                    required = true) final @NotNull @PathVariable Long cartId) {
        return cartService.deleteCart(cartId) ? HttpResponse.ok() : HttpResponse.notFound();
    }

    @Get("/{cartId}")
    @Operation(
            summary = "Get a cart by ID",
            description = "Returns a single shopping cart")
    @ApiResponse(
            responseCode = "200",
            description = "Cart found",
            content = @Content(schema = @Schema(implementation = CartDto.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Cart not found")
    public @NotNull HttpResponse<CartDto> getCartById(
            @Parameter(
                    description = "ID of cart to return",
                    required = true) final @NotNull @PathVariable Long cartId) {
        return cartService.getCartById(cartId)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Post("/{cartId}/add_product")
    @Operation(
            summary = "Add product to cart",
            description = "Adds a single product to the shopping cart")
    @ApiResponse(
            responseCode = "200",
            description = "Product added to cart successfully",
            content = @Content(schema = @Schema(implementation = CartDto.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Cart or product not found")
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input")
    public @NotNull HttpResponse<CartDto> addProductToCart(
            @Parameter(
                    description = "ID of cart to add product to",
                    required = true) final @NotNull @PathVariable Long cartId,
            @Parameter(
                    description = "Product to add to cart",
                    required = true) final @NotNull @Body AddProductRequest request
    ) {
        return cartService.addProductToCart(cartId, request.getProductId())
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Delete("/{cartId}/products/{productId}")
    @Operation(
            summary = "Remove product from cart",
            description = "Removes a single product from the shopping cart")
    @ApiResponse(
            responseCode = "200",
            description = "Product removed from cart successfully",
            content = @Content(schema = @Schema(implementation = CartDto.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Cart not found")
    public @NotNull HttpResponse<CartDto> removeProductFromCart(
            @Parameter(
                    description = "ID of cart to remove product from",
                    required = true) final @NotNull @PathVariable Long cartId,
            @Parameter(
                    description = "ID of product to remove from cart",
                    required = true) final @NotNull @PathVariable Long productId
    ) {
        return cartService.removeProductFromCart(cartId, productId)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Post("/{cartId}/add_recipe")
    @Operation(
            summary = "Add recipe to cart",
            description = "Adds a recipe and its ingredients to the shopping cart")
    @ApiResponse(
            responseCode = "200",
            description = "Recipe added to cart successfully",
            content = @Content(schema = @Schema(implementation = CartDto.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Cart or recipe not found")
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input")
    public @NotNull HttpResponse<CartDto> addRecipeToCart(
            @Parameter(
                    description = "ID of cart to add recipe to",
                    required = true) final @NotNull @PathVariable Long cartId,
            @Parameter(
                    description = "Recipe to add to cart",
                    required = true) final @NotNull @Body AddRecipeRequest request
    ) {
        return cartService.addRecipeToCart(cartId, request.getRecipeId())
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Delete("/{cartId}/recipes/{recipeId}")
    @Operation(
            summary = "Remove recipe from cart", description = "Removes a recipe and its ingredients from the shopping cart")
    @ApiResponse(
            responseCode = "200",
            description = "Recipe removed from cart successfully",
            content = @Content(schema = @Schema(implementation = CartDto.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Cart or recipe not found")
    public @NotNull HttpResponse<CartDto> removeRecipeFromCart(
            @Parameter(
                    description = "ID of cart to remove recipe from",
                    required = true) final @NotNull @PathVariable Long cartId,
            @Parameter(
                    description = "ID of recipe to remove from cart",
                    required = true) final @NotNull @PathVariable Long recipeId
    ) {
        return cartService.removeRecipeFromCart(cartId, recipeId)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }
}