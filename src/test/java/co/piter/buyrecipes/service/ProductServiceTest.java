package co.piter.buyrecipes.service;

import co.piter.buyrecipes.dto.CreateProductRequest;
import co.piter.buyrecipes.dto.ProductDto;
import co.piter.buyrecipes.dto.UpdateProductRequest;
import co.piter.buyrecipes.entity.Product;
import co.piter.buyrecipes.repo.ProductRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class
ProductServiceTest {

    @Mock
    private ProductRepo productRepo;

    private ProductService productService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        productService = new ProductService(productRepo);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testCreateProduct() {
        final CreateProductRequest request = new CreateProductRequest("Test Product", 100);
        final Product savedProduct = new Product("Test Product", 100);
        savedProduct.setId(1L);

        when(productRepo.save(any(Product.class))).thenReturn(savedProduct);

        final ProductDto result = productService.createProduct(request);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getPriceInCents()).isEqualTo(100);

        final ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepo).save(productCaptor.capture());

        final Product capturedProduct = productCaptor.getValue();
        assertThat(capturedProduct.getName()).isEqualTo("Test Product");
        assertThat(capturedProduct.getPriceInCents()).isEqualTo(100);
    }

    @Test
    void testGetAllProducts() {
        final Product product1 = new Product("Product 1", 100);
        product1.setId(1L);
        final Product product2 = new Product("Product 2", 200);
        product2.setId(2L);

        when(productRepo.findAll()).thenReturn(List.of(product1, product2));

        final List<ProductDto> result = productService.getAllProducts();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Product 1");
        assertThat(result.get(1).getName()).isEqualTo("Product 2");
    }

    @Test
    void testGetProductById() {
        final Product product = new Product("Test Product", 100);
        product.setId(1L);

        when(productRepo.findById(1L)).thenReturn(Optional.of(product));

        final Optional<ProductDto> result = productService.getProductById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Product");
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepo.findById(1L)).thenReturn(Optional.empty());
        assertThat(productService.getProductById(1L)).isEmpty();
    }

    @Test
    void testUpdateProduct() {
        final Product existingProduct = new Product("Old Name", 100);
        existingProduct.setId(1L);

        final Product updatedProduct = new Product("New Name", 200);
        updatedProduct.setId(1L);

        final UpdateProductRequest request = new UpdateProductRequest("New Name", 200);

        when(productRepo.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepo.save(any(Product.class))).thenReturn(updatedProduct);

        final Optional<ProductDto> result = productService.updateProduct(1L, request);
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("New Name");
        assertThat(result.get().getPriceInCents()).isEqualTo(200);
    }

    @Test
    void testUpdateProductNotFound() {
        final UpdateProductRequest request = new UpdateProductRequest("New Name", 200);
        when(productRepo.findById(1L)).thenReturn(Optional.empty());
        assertThat(productService.updateProduct(1L, request)).isEmpty();
    }

    @Test
    void testDeleteProduct() {
        final Product product = new Product("Test Product", 100);
        product.setId(1L);
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        assertThat(productService.deleteProduct(1L)).isTrue();
        verify(productRepo).delete(product);
    }

    @Test
    void testDeleteProductNotFound() {
        when(productRepo.findById(1L)).thenReturn(Optional.empty());
        assertThat(productService.deleteProduct(1L)).isFalse();
        verify(productRepo, never()).delete(any());
    }
}