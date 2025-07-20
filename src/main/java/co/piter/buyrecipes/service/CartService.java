package co.piter.buyrecipes.service;

import co.piter.buyrecipes.dto.CartDto;
import co.piter.buyrecipes.dto.CreateCartRequest;
import co.piter.buyrecipes.dto.ProductDto;
import co.piter.buyrecipes.dto.UpdateCartRequest;
import co.piter.buyrecipes.entity.Cart;
import co.piter.buyrecipes.entity.CartItem;
import co.piter.buyrecipes.entity.Product;
import co.piter.buyrecipes.entity.RecipeIngredient;
import co.piter.buyrecipes.repo.CartItemRepository;
import co.piter.buyrecipes.repo.CartRepo;
import co.piter.buyrecipes.repo.ProductRepo;
import co.piter.buyrecipes.repo.RecipeIngredientRepo;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Singleton
public class CartService {

    private final @NotNull CartRepo cartRepo;
    private final @NotNull CartItemRepository cartItemRepository;
    private final @NotNull ProductRepo productRepo;
    private final @NotNull RecipeIngredientRepo recipeIngredientRepo;

    public CartService(
            final @NotNull CartRepo cartRepo,
            final @NotNull CartItemRepository cartItemRepository,
            final @NotNull ProductRepo productRepo,
            final @NotNull RecipeIngredientRepo recipeIngredientRepo
    ) {
        this.cartRepo = requireNonNull(cartRepo);
        this.cartItemRepository = requireNonNull(cartItemRepository);
        this.productRepo = requireNonNull(productRepo);
        this.recipeIngredientRepo = requireNonNull(recipeIngredientRepo);
    }

    public @NotNull List<CartDto> getAllCarts() {
        return Optional.ofNullable(cartRepo.findAll())
                .map(carts -> carts.stream()
                        .map(this::toDto)
                        .toList())
                .orElse(List.of());
    }

    public @NotNull Optional<CartDto> getCartById(final @NotNull Long cartId) {
        return cartRepo.findById(cartId).map(this::toDto);
    }

    @Transactional
    public @NotNull CartDto createCart(final @NotNull CreateCartRequest request) {
        return toDto(cartRepo.save(new Cart(request.getTotalInCents())));
    }

    @Transactional
    public @NotNull Optional<CartDto> updateCart(final @NotNull Long cartId, final @NotNull UpdateCartRequest request) {
        return cartRepo.findById(cartId)
                .map(cart -> {
                    cart.setTotalInCents(request.getTotalInCents());
                    return cart;
                })
                .map(cartRepo::save)
                .map(this::toDto);
    }

    @Transactional
    public boolean deleteCart(final @NotNull Long cartId) {
        return cartRepo.findById(cartId).map(cart -> {
            final List<CartItem> cartItems = cartItemRepository.findByCartId(cartId);
            if (cartItems != null && !cartItems.isEmpty()) {
                cartItemRepository.deleteAll(cartItems);
            }
            cartRepo.delete(cart);
            return true;
        }).orElse(false);
    }

    @Transactional
    public @NotNull Optional<CartDto> addRecipeToCart(
            final @NotNull Long cartId,
            final @NotNull Long recipeId
    ) {
        return cartRepo.findById(cartId)
                .flatMap(cart -> Optional.ofNullable(recipeIngredientRepo.findByRecipeId(recipeId))
                        .filter(ingredients -> !ingredients.isEmpty())
                        .map(ingredients -> {
                            cartItemRepository.saveAll(ingredients.stream()
                                    .map(ingredient -> new CartItem(cartId, ingredient.getProductId()))
                                    .toList());
                            updateCartTotalForCart(cart);
                            return cart;
                        }))
                .map(cartRepo::save)
                .map(this::toDto);
    }

    @Transactional
    public @NotNull Optional<CartDto> addProductToCart(
            final @NotNull Long cartId,
            final @NotNull Long productId
    ) {
        return cartRepo.findById(cartId)
                .flatMap(cart -> productRepo.findById(productId)
                        .map(product -> {
                            cartItemRepository.save(new CartItem(cartId, productId));
                            updateCartTotalForCart(cart);
                            return cart;
                        }))
                .map(cartRepo::save)
                .map(this::toDto);
    }

    @Transactional
    public @NotNull Optional<CartDto> removeProductFromCart(
            final @NotNull Long cartId,
            final @NotNull Long productId
    ) {
        return cartRepo.findById(cartId)
                .map(cart -> Optional.ofNullable(cartItemRepository.findByCartIdAndProductId(cartId, productId))
                        .map(cartItem -> {
                            cartItemRepository.delete(cartItem);
                            return cart;
                        })
                        .orElse(cart))
                .map(cart -> {
                    updateCartTotalForCart(cart);
                    return cartRepo.save(cart);
                })
                .map(this::toDto);
    }

    @Transactional
    public @NotNull Optional<CartDto> removeRecipeFromCart(
            final @NotNull Long cartId,
            final @NotNull Long recipeId
    ) {
        return cartRepo.findById(cartId).map(cart -> {
            final List<RecipeIngredient> recipeIngredients = recipeIngredientRepo.findByRecipeId(recipeId);
            if (recipeIngredients == null || recipeIngredients.isEmpty()) {
                return toDto(cart);
            }

            final List<CartItem> itemsToRemove = recipeIngredients.stream()
                    .map(ingredient -> cartItemRepository.findByCartIdAndProductId(cartId, ingredient.getProductId()))
                    .filter(Objects::nonNull)
                    .toList();

            if (!itemsToRemove.isEmpty()) {
                cartItemRepository.deleteAll(itemsToRemove);
            }

            updateCartTotalForCart(cart);
            return toDto(cartRepo.save(cart));
        });
    }


    private void updateCartTotalForCart(final @NotNull Cart cart) {
        cart.setTotalInCents(Optional.ofNullable(cartItemRepository.findByCartId(requireNonNull(cart.getId())))
                .orElse(List.of())
                .stream()
                .map(item -> productRepo.findById(item.getProductId()))
                .flatMap(Optional::stream)
                .mapToInt(Product::getPriceInCents)
                .sum());
    }

    private CartDto toDto(final @NotNull Cart cart) {
        return new CartDto(
                requireNonNull(cart.getId()),
                cart.getTotalInCents(),
                Optional.ofNullable(cartItemRepository.findByCartId(requireNonNull(cart.getId())))
                        .orElse(List.of())
                        .stream()
                        .map(this::toDto)
                        .toList());
    }

    private @NotNull ProductDto toDto(final @NotNull CartItem cartItem) {
        return productRepo.findById(cartItem.getProductId())
                .map(p -> new ProductDto(requireNonNull(p.getId()), p.getName(), p.getPriceInCents()))
                .orElseThrow(() -> new RuntimeException("Product not found for cart item: " + cartItem.getProductId()));
    }
}