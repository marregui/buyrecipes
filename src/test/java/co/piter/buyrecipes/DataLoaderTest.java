package co.piter.buyrecipes;

import co.piter.buyrecipes.entity.Cart;
import co.piter.buyrecipes.entity.Product;
import co.piter.buyrecipes.entity.Recipe;
import co.piter.buyrecipes.entity.RecipeIngredient;
import co.piter.buyrecipes.repo.CartRepo;
import co.piter.buyrecipes.repo.ProductRepo;
import co.piter.buyrecipes.repo.RecipeIngredientRepo;
import co.piter.buyrecipes.repo.RecipeRepo;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DataLoaderTest {

    @Mock
    private ProductRepo productRepo;

    @Mock
    private RecipeRepo recipeRepo;

    @Mock
    private RecipeIngredientRepo recipeIngredientRepo;

    @Mock
    private CartRepo cartRepo;

    @Mock
    private ServerStartupEvent serverStartupEvent;

    private DataLoader dataLoader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dataLoader = new DataLoader(productRepo, recipeRepo, recipeIngredientRepo, cartRepo);
    }

    @Test
    void dataLoader_ShouldImplementApplicationEventListener() {
        assertThat(dataLoader).isInstanceOf(ApplicationEventListener.class);
    }

    @Test
    void onApplicationEvent_WhenNoProductsExist_ShouldCheckProductCount() {
        when(productRepo.count()).thenReturn(0L);
        try {
            dataLoader.onApplicationEvent(serverStartupEvent);
        } catch (final Exception expected) {
            // expected
        }
        verify(productRepo, atLeast(1)).count();
    }

    @Test
    void onApplicationEvent_WhenProductsExist_ShouldNotLoadData() {
        when(productRepo.count()).thenReturn(5L);
        dataLoader.onApplicationEvent(serverStartupEvent);
        verify(productRepo).count();
        verify(productRepo, never()).save(any(Product.class));
        verify(recipeRepo, never()).save(any(Recipe.class));
        verify(recipeIngredientRepo, never()).save(any(RecipeIngredient.class));
        verify(cartRepo, never()).save(any(Cart.class));
    }

    @Test
    void constructor_ShouldCreateDataLoader() {
        final DataLoader testDataLoader = new DataLoader(
                productRepo,
                recipeRepo,
                recipeIngredientRepo,
                cartRepo);
        assertThat(testDataLoader).isNotNull();
        assertThat(testDataLoader).isInstanceOf(ApplicationEventListener.class);
    }
}