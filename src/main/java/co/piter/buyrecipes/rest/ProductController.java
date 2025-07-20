package co.piter.buyrecipes.rest;

import co.piter.buyrecipes.dto.CreateProductRequest;
import co.piter.buyrecipes.dto.ProductDto;
import co.piter.buyrecipes.dto.UpdateProductRequest;
import co.piter.buyrecipes.service.ProductService;
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

@Controller("/products")
@Singleton
@Tag(name = "Products", description = "Product management")
public class ProductController {

    private final @NotNull ProductService productService;

    public ProductController(final @NotNull ProductService productService) {
        this.productService = requireNonNull(productService);
    }

    @Post
    @Operation(
            summary = "Create a new product",
            description = "Creates a new product in the system")
    @ApiResponse(
            responseCode = "200",
            description = "Product created successfully",
            content = @Content(schema = @Schema(implementation = ProductDto.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input")
    public @NotNull HttpResponse<ProductDto> createProduct(
            @Parameter(
                    description = "Product to create",
                    required = true) final @NotNull @Body CreateProductRequest request) {
        return HttpResponse.ok(productService.createProduct(request));
    }

    @Get
    @Operation(
            summary = "List all products",
            description = "Returns a list of all products in the system")
    @ApiResponse(
            responseCode = "200",
            description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ProductDto.class)))
    public @NotNull HttpResponse<List<ProductDto>> getAllProducts() {
        return HttpResponse.ok(productService.getAllProducts());
    }

    @Get("/{id}")
    @Operation(
            summary = "Get a product by ID",
            description = "Returns a single product")
    @ApiResponse(
            responseCode = "200",
            description = "Product found",
            content = @Content(schema = @Schema(implementation = ProductDto.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Product not found")
    public @NotNull HttpResponse<ProductDto> getProductById(
            @Parameter(
                    description = "ID of product to return",
                    required = true) final @NotNull @PathVariable Long id) {
        return productService.getProductById(id)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Put("/{id}")
    @Operation(
            summary = "Update a product",
            description = "Updates an existing product")
    @ApiResponse(
            responseCode = "200",
            description = "Product updated successfully",
            content = @Content(schema = @Schema(implementation = ProductDto.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Product not found")
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input")
    public @NotNull HttpResponse<ProductDto> updateProduct(
            @Parameter(
                    description = "ID of product to update",
                    required = true) final @NotNull @PathVariable Long id,
            @Parameter(
                    description = "Updated product information",
                    required = true) final @NotNull @Body UpdateProductRequest request
    ) {
        return productService.updateProduct(id, request)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Delete("/{id}")
    @Operation(
            summary = "Delete a product",
            description = "Deletes a product from the system")
    @ApiResponse(
            responseCode = "200",
            description = "Product deleted successfully")
    @ApiResponse(
            responseCode = "404",
            description = "Product not found")
    public @NotNull HttpResponse<Void> deleteProduct(
            @Parameter(
                    description = "ID of product to delete",
                    required = true) final @NotNull @PathVariable Long id) {
        return productService.deleteProduct(id) ? HttpResponse.ok() : HttpResponse.notFound();
    }
}