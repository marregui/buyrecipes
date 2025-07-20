package co.piter.buyrecipes.repo;

import co.piter.buyrecipes.entity.Product;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    // JpaRepository provides:
    // - save(Cart cart): create/update a cart
    // - findById(Long id): find a cart by ID
    // - findAll(): get all carts
    // - deleteById(Long id): delete a cart by ID
    // - existsById(Long id): check if a cart exists by ID
}