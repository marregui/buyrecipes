package co.piter.buyrecipes.service;

import co.piter.buyrecipes.dto.CreateProductRequest;
import co.piter.buyrecipes.dto.ProductDto;
import co.piter.buyrecipes.dto.UpdateProductRequest;
import co.piter.buyrecipes.entity.Product;
import co.piter.buyrecipes.repo.ProductRepo;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Singleton
public class ProductService {

    private final @NotNull ProductRepo productRepo;

    public ProductService(final @NotNull ProductRepo productRepo) {
        this.productRepo = requireNonNull(productRepo);
    }

    @Transactional
    public @NotNull ProductDto createProduct(final @NotNull CreateProductRequest request) {
        return toDto(productRepo.save(new Product(request.getName(), request.getPriceInCents())));
    }

    public @NotNull List<ProductDto> getAllProducts() {
        return productRepo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public @NotNull Optional<ProductDto> getProductById(final @NotNull Long id) {
        return productRepo.findById(id).map(this::toDto);
    }

    @Transactional
    public @NotNull Optional<ProductDto> updateProduct(final @NotNull Long id, final @NotNull UpdateProductRequest request) {
        return productRepo.findById(id)
                .map(product -> {
                    product.setName(request.getName());
                    product.setPriceInCents(request.getPriceInCents());
                    Product savedProduct = productRepo.save(product);
                    return toDto(savedProduct);
                });
    }

    @Transactional
    public boolean deleteProduct(final @NotNull Long id) {
        return productRepo.findById(id).map(product -> {
            productRepo.delete(product);
            return true;
        }).orElse(false);
    }

    private @NotNull ProductDto toDto(final @NotNull Product product) {
        return new ProductDto(requireNonNull(product.getId()), product.getName(), product.getPriceInCents());
    }
}