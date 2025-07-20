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

class CartServiceTest {

    @Mock
    private CartRepo cartRepo;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private RecipeIngredientRepo recipeIngredientRepo;

    private CartService cartService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        cartService = new CartService(
                cartRepo,
                cartItemRepository,
                productRepo,
                recipeIngredientRepo);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getCartById_WhenCartExists_ShouldReturnCartWithItems() {
        final Cart cart = new Cart(500);
        cart.setId(1L);

        final CartItem item1 = new CartItem(1L, 1L);
        item1.setId(1L);
        final CartItem item2 = new CartItem(1L, 2L);
        item2.setId(2L);
        final List<CartItem> cartItems = List.of(item1, item2);

        final Product flour = new Product("Flour", 299);
        flour.setId(1L);
        final Product sugar = new Product("Sugar", 199);
        sugar.setId(2L);

        when(cartRepo.findById(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(1L)).thenReturn(cartItems);
        when(productRepo.findById(1L)).thenReturn(Optional.of(flour));
        when(productRepo.findById(2L)).thenReturn(Optional.of(sugar));

        final Optional<CartDto> result = cartService.getCartById(1L);
        assertThat(result).isPresent();

        final CartDto cartDto = result.get();
        assertThat(cartDto.getId()).isEqualTo(1L);
        assertThat(cartDto.getTotalInCents()).isEqualTo(500);
        assertThat(cartDto.getItems()).hasSize(2);

        final ProductDto firstItem = cartDto.getItems().get(0);
        assertThat(firstItem.getId()).isEqualTo(1L);
        assertThat(firstItem.getName()).isEqualTo("Flour");
        assertThat(firstItem.getPriceInCents()).isEqualTo(299);

        final ProductDto secondItem = cartDto.getItems().get(1);
        assertThat(secondItem.getId()).isEqualTo(2L);
        assertThat(secondItem.getName()).isEqualTo("Sugar");
        assertThat(secondItem.getPriceInCents()).isEqualTo(199);
    }

    @Test
    void getCartById_WhenCartNotFound_ShouldReturnEmpty() {
        when(cartRepo.findById(999L)).thenReturn(Optional.empty());
        final Optional<CartDto> result = cartService.getCartById(999L);
        assertThat(result).isEmpty();
    }

    @Test
    void addRecipeToCart_WhenCartAndRecipeExist_ShouldAddIngredientsToCart() {
        final Cart cart = new Cart(0);
        cart.setId(1L);

        final RecipeIngredient ingredient1 = new RecipeIngredient(1L, 1L, 2);
        final RecipeIngredient ingredient2 = new RecipeIngredient(1L, 2L, 1);
        final List<RecipeIngredient> recipeIngredients = List.of(ingredient1, ingredient2);

        final Product flour = new Product("Flour", 299);
        flour.setId(1L);
        final Product sugar = new Product("Sugar", 199);
        sugar.setId(2L);

        final CartItem cartItem1 = new CartItem(1L, 1L);
        final CartItem cartItem2 = new CartItem(1L, 2L);
        final List<CartItem> finalCartItems = List.of(cartItem1, cartItem2);

        when(cartRepo.findById(1L)).thenReturn(Optional.of(cart));
        when(recipeIngredientRepo.findByRecipeId(1L)).thenReturn(recipeIngredients);
        when(cartItemRepository.findByCartId(1L)).thenReturn(finalCartItems);
        when(productRepo.findById(1L)).thenReturn(Optional.of(flour));
        when(productRepo.findById(2L)).thenReturn(Optional.of(sugar));
        when(cartRepo.save(any(Cart.class))).thenReturn(cart);

        final Optional<CartDto> result = cartService.addRecipeToCart(1L, 1L);
        assertThat(result).isPresent();

        verify(cartItemRepository).saveAll(any(List.class));

        final ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepo).save(cartCaptor.capture());
        assertThat(cartCaptor.getValue().getTotalInCents()).isEqualTo(498);
    }

    @Test
    void addRecipeToCart_WhenCartNotFound_ShouldReturnEmpty() {
        when(cartRepo.findById(999L)).thenReturn(Optional.empty());
        assertThat(cartService.addRecipeToCart(999L, 1L)).isEmpty();
        verify(cartItemRepository, never()).saveAll(any(List.class));
    }

    @Test
    void addRecipeToCart_WhenRecipeNotFound_ShouldReturnEmpty() {
        final Cart cart = new Cart(0);
        cart.setId(1L);
        when(cartRepo.findById(1L)).thenReturn(Optional.of(cart));
        when(recipeIngredientRepo.findByRecipeId(999L)).thenReturn(List.of());
        assertThat(cartService.addRecipeToCart(1L, 999L)).isEmpty();
        verify(cartItemRepository, never()).saveAll(any(List.class));
    }

    @Test
    void addProductToCart_WhenCartAndProductExist_ShouldAddProductToCart() {
        final Cart cart = new Cart(0);
        cart.setId(1L);

        final Product flour = new Product("Flour", 299);
        flour.setId(1L);

        final CartItem cartItem = new CartItem(1L, 1L);
        final List<CartItem> finalCartItems = List.of(cartItem);

        when(cartRepo.findById(1L)).thenReturn(Optional.of(cart));
        when(productRepo.findById(1L)).thenReturn(Optional.of(flour));
        when(cartItemRepository.findByCartId(1L)).thenReturn(finalCartItems);
        when(cartRepo.save(any(Cart.class))).thenReturn(cart);

        final Optional<CartDto> result = cartService.addProductToCart(1L, 1L);
        assertThat(result).isPresent();

        verify(cartItemRepository).save(any(CartItem.class));

        final ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepo).save(cartCaptor.capture());
        assertThat(cartCaptor.getValue().getTotalInCents()).isEqualTo(299);
    }

    @Test
    void addProductToCart_WhenCartNotFound_ShouldReturnEmpty() {
        when(cartRepo.findById(999L)).thenReturn(Optional.empty());
        assertThat(cartService.addProductToCart(999L, 1L)).isEmpty();
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void addProductToCart_WhenProductNotFound_ShouldReturnEmpty() {
        final Cart cart = new Cart(0);
        cart.setId(1L);
        when(cartRepo.findById(1L)).thenReturn(Optional.of(cart));
        when(productRepo.findById(999L)).thenReturn(Optional.empty());
        assertThat(cartService.addProductToCart(1L, 999L)).isEmpty();
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void removeProductFromCart_WhenCartAndProductExist_ShouldRemoveProductFromCart() {
        final Cart cart = new Cart(299);
        cart.setId(1L);

        final CartItem existingCartItem = new CartItem(1L, 1L);
        existingCartItem.setId(1L);

        when(cartRepo.findById(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(existingCartItem);
        when(cartItemRepository.findByCartId(1L)).thenReturn(List.of());
        when(cartRepo.save(any(Cart.class))).thenReturn(cart);

        final Optional<CartDto> result = cartService.removeProductFromCart(1L, 1L);
        assertThat(result).isPresent();

        verify(cartItemRepository).delete(existingCartItem);

        final ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepo).save(cartCaptor.capture());
        assertThat(cartCaptor.getValue().getTotalInCents()).isEqualTo(0);
    }

    @Test
    void removeProductFromCart_WhenCartNotFound_ShouldReturnEmpty() {
        when(cartRepo.findById(999L)).thenReturn(Optional.empty());
        assertThat(cartService.removeProductFromCart(999L, 1L)).isEmpty();
        verify(cartItemRepository, never()).delete(any(CartItem.class));
    }

    @Test
    void removeProductFromCart_WhenProductNotInCart_ShouldReturnCartUnchanged() {
        final Cart cart = new Cart(299);
        cart.setId(1L);

        when(cartRepo.findById(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(1L, 999L)).thenReturn(null);
        when(cartItemRepository.findByCartId(1L)).thenReturn(List.of());
        when(cartRepo.save(any(Cart.class))).thenReturn(cart);

        final Optional<CartDto> result = cartService.removeProductFromCart(1L, 999L);
        assertThat(result).isPresent();

        verify(cartItemRepository, never()).delete(any(CartItem.class));

        final ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepo).save(cartCaptor.capture());
        assertThat(cartCaptor.getValue().getTotalInCents()).isEqualTo(0);
    }

    @Test
    void removeRecipeFromCart_WhenCartAndRecipeExist_ShouldRemoveIngredientsFromCart() {
        final Cart cart = new Cart(498);
        cart.setId(1L);
        final RecipeIngredient ingredient1 = new RecipeIngredient(1L, 1L, 2);
        final RecipeIngredient ingredient2 = new RecipeIngredient(1L, 2L, 1);
        final List<RecipeIngredient> recipeIngredients = List.of(ingredient1, ingredient2);

        final CartItem cartItem1 = new CartItem(1L, 1L);
        cartItem1.setId(1L);
        final CartItem cartItem2 = new CartItem(1L, 1L);
        cartItem2.setId(2L);
        final CartItem cartItem3 = new CartItem(1L, 2L);
        cartItem3.setId(3L);

        final Product flour = new Product("Flour", 299);
        flour.setId(1L);
        final Product sugar = new Product("Sugar", 199);
        sugar.setId(2L);

        when(cartRepo.findById(1L)).thenReturn(Optional.of(cart));
        when(recipeIngredientRepo.findByRecipeId(1L)).thenReturn(recipeIngredients);
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L))
                .thenReturn(cartItem1);
        when(cartItemRepository.findByCartIdAndProductId(1L, 2L))
                .thenReturn(cartItem3);
        when(cartItemRepository.findByCartId(1L)).thenReturn(List.of());
        when(cartRepo.save(any(Cart.class))).thenReturn(cart);

        assertThat(cartService.removeRecipeFromCart(1L, 1L)).isPresent();

        verify(cartItemRepository).deleteAll(any(List.class));

        final ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepo).save(cartCaptor.capture());
        assertThat(cartCaptor.getValue().getTotalInCents()).isEqualTo(0);
    }

    @Test
    void removeRecipeFromCart_WhenCartNotFound_ShouldReturnEmpty() {
        when(cartRepo.findById(999L)).thenReturn(Optional.empty());
        assertThat(cartService.removeRecipeFromCart(999L, 1L)).isEmpty();
        verify(cartItemRepository, never()).deleteById(any(Long.class));
    }

    @Test
    void getAllCarts_WhenCartsExist_ShouldReturnAllCarts() {
        final Cart cart1 = new Cart(500);
        cart1.setId(1L);
        final Cart cart2 = new Cart(300);
        cart2.setId(2L);
        final List<Cart> carts = List.of(cart1, cart2);
        when(cartRepo.findAll()).thenReturn(carts);
        when(cartItemRepository.findByCartId(1L)).thenReturn(List.of());
        when(cartItemRepository.findByCartId(2L)).thenReturn(List.of());
        final List<CartDto> result = cartService.getAllCarts();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getTotalInCents()).isEqualTo(500);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getTotalInCents()).isEqualTo(300);
    }

    @Test
    void getAllCarts_WhenNoCartsExist_ShouldReturnEmptyList() {
        when(cartRepo.findAll()).thenReturn(List.of());
        assertThat(cartService.getAllCarts()).isEmpty();
    }

    @Test
    void createCart_ShouldCreateAndReturnCart() {
        final CreateCartRequest request = new CreateCartRequest(500);
        final Cart cart = new Cart(500);
        cart.setId(1L);
        when(cartRepo.save(any(Cart.class))).thenReturn(cart);
        when(cartItemRepository.findByCartId(1L)).thenReturn(List.of());
        final CartDto result = cartService.createCart(request);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTotalInCents()).isEqualTo(500);
        assertThat(result.getItems()).isEmpty();
        final ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepo).save(cartCaptor.capture());
        assertThat(cartCaptor.getValue().getTotalInCents()).isEqualTo(500);
    }

    @Test
    void updateCart_WhenCartExists_ShouldUpdateAndReturnCart() {
        final UpdateCartRequest request = new UpdateCartRequest(600);
        final Cart existingCart = new Cart(500);
        existingCart.setId(1L);
        final Cart updatedCart = new Cart(600);
        updatedCart.setId(1L);
        when(cartRepo.findById(1L)).thenReturn(Optional.of(existingCart));
        when(cartRepo.save(any(Cart.class))).thenReturn(updatedCart);
        when(cartItemRepository.findByCartId(1L)).thenReturn(List.of());
        final Optional<CartDto> result = cartService.updateCart(1L, request);
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getTotalInCents()).isEqualTo(600);
        final ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepo).save(cartCaptor.capture());
        assertThat(cartCaptor.getValue().getTotalInCents()).isEqualTo(600);
    }

    @Test
    void updateCart_WhenCartNotFound_ShouldReturnEmpty() {
        final UpdateCartRequest request = new UpdateCartRequest(600);
        when(cartRepo.findById(999L)).thenReturn(Optional.empty());
        assertThat(cartService.updateCart(999L, request)).isEmpty();
        verify(cartRepo, never()).save(any(Cart.class));
    }

    @Test
    void deleteCart_WhenCartExists_ShouldDeleteCartAndReturnTrue() {
        final CartItem item1 = new CartItem(1L, 1L);
        item1.setId(1L);
        final CartItem item2 = new CartItem(1L, 2L);
        item2.setId(2L);
        final List<CartItem> cartItems = List.of(item1, item2);
        final Cart cart = new Cart(0);
        cart.setId(1L);
        when(cartRepo.findById(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(1L)).thenReturn(cartItems);
        assertThat(cartService.deleteCart(1L)).isTrue();
        verify(cartItemRepository).deleteAll(any(List.class));
        verify(cartRepo).delete(any(Cart.class));
    }

    @Test
    void deleteCart_WhenCartNotFound_ShouldReturnFalse() {
        when(cartRepo.findById(999L)).thenReturn(Optional.empty());
        assertThat(cartService.deleteCart(999L)).isFalse();
        verify(cartItemRepository, never()).deleteById(any(Long.class));
        verify(cartRepo, never()).delete(any(Cart.class));
    }
}