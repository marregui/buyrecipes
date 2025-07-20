package co.piter.buyrecipes.service;

import co.piter.buyrecipes.dto.CreateRecipeIngredientRequest;
import co.piter.buyrecipes.dto.RecipeIngredientDto;
import co.piter.buyrecipes.dto.UpdateRecipeIngredientRequest;
import co.piter.buyrecipes.entity.RecipeIngredient;
import co.piter.buyrecipes.repo.ProductRepo;
import co.piter.buyrecipes.repo.RecipeIngredientRepo;
import co.piter.buyrecipes.repo.RecipeRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RecipeIngredientServiceTest {

    @Mock
    private RecipeIngredientRepo recipeIngredientRepo;

    @Mock
    private RecipeRepo recipeRepo;

    @Mock
    private ProductRepo productRepo;

    private RecipeIngredientService recipeIngredientService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        recipeIngredientService = new RecipeIngredientService(recipeIngredientRepo, recipeRepo, productRepo);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testGetAllRecipeIngredients() {
        final RecipeIngredient ingredient1 = new RecipeIngredient(1L, 1L, 2, "cups");
        ingredient1.setId(1L);
        final RecipeIngredient ingredient2 = new RecipeIngredient(2L, 2L, 1, "grams");
        ingredient2.setId(2L);

        when(recipeIngredientRepo.findAll()).thenReturn(List.of(ingredient1, ingredient2));

        final List<RecipeIngredientDto> result = recipeIngredientService.getAllRecipeIngredients();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getRecipeId()).isEqualTo(1L);
        assertThat(result.get(0).getProductId()).isEqualTo(1L);
        assertThat(result.get(0).getQuantity()).isEqualTo(2);
        assertThat(result.get(0).getUnit()).isEqualTo("cups");
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getRecipeId()).isEqualTo(2L);
        assertThat(result.get(1).getProductId()).isEqualTo(2L);
        assertThat(result.get(1).getQuantity()).isEqualTo(1);
        assertThat(result.get(1).getUnit()).isEqualTo("grams");
    }

    @Test
    void testGetRecipeIngredientById() {
        final RecipeIngredient ingredient = new RecipeIngredient(1L, 1L, 2, "tablespoons");
        ingredient.setId(1L);

        when(recipeIngredientRepo.findById(1L)).thenReturn(Optional.of(ingredient));

        final Optional<RecipeIngredientDto> result = recipeIngredientService.getRecipeIngredientById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getRecipeId()).isEqualTo(1L);
        assertThat(result.get().getProductId()).isEqualTo(1L);
        assertThat(result.get().getQuantity()).isEqualTo(2);
        assertThat(result.get().getUnit()).isEqualTo("tablespoons");
    }

    @Test
    void testGetRecipeIngredientByIdNotFound() {
        when(recipeIngredientRepo.findById(999L)).thenReturn(Optional.empty());
        assertThat(recipeIngredientService.getRecipeIngredientById(999L)).isEmpty();
    }

    @Test
    void testGetRecipeIngredientsByRecipeId() {
        final RecipeIngredient ingredient1 = new RecipeIngredient(1L, 1L, 2, "pieces");
        ingredient1.setId(1L);
        final RecipeIngredient ingredient2 = new RecipeIngredient(1L, 2L, 1, null);
        ingredient2.setId(2L);

        when(recipeIngredientRepo.findByRecipeId(1L)).thenReturn(List.of(ingredient1, ingredient2));

        final List<RecipeIngredientDto> result = recipeIngredientService.getRecipeIngredientsByRecipeId(1L);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getRecipeId()).isEqualTo(1L);
        assertThat(result.get(0).getProductId()).isEqualTo(1L);
        assertThat(result.get(0).getUnit()).isEqualTo("pieces");
        assertThat(result.get(1).getRecipeId()).isEqualTo(1L);
        assertThat(result.get(1).getProductId()).isEqualTo(2L);
        assertThat(result.get(1).getUnit()).isEqualTo("");
    }

    @Test
    void testGetRecipeIngredientsByProductId() {
        final RecipeIngredient ingredient1 = new RecipeIngredient(1L, 1L, 2, "teaspoons");
        ingredient1.setId(1L);
        final RecipeIngredient ingredient2 = new RecipeIngredient(2L, 1L, 1, "pinches");
        ingredient2.setId(2L);

        when(recipeIngredientRepo.findByProductId(1L)).thenReturn(List.of(ingredient1, ingredient2));

        final List<RecipeIngredientDto> result = recipeIngredientService.getRecipeIngredientsByProductId(1L);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getProductId()).isEqualTo(1L);
        assertThat(result.get(0).getRecipeId()).isEqualTo(1L);
        assertThat(result.get(0).getUnit()).isEqualTo("teaspoons");
        assertThat(result.get(1).getProductId()).isEqualTo(1L);
        assertThat(result.get(1).getRecipeId()).isEqualTo(2L);
        assertThat(result.get(1).getUnit()).isEqualTo("pinches");
    }

    @Test
    void testCreateRecipeIngredient() {
        final CreateRecipeIngredientRequest request = new CreateRecipeIngredientRequest(1L, 1L, 2, "cups");
        final RecipeIngredient savedIngredient = new RecipeIngredient(1L, 1L, 2, "cups");
        savedIngredient.setId(1L);

        when(recipeRepo.findById(1L)).thenReturn(Optional.of(new co.piter.buyrecipes.entity.Recipe("Test Recipe", "Test Description")));
        when(productRepo.findById(1L)).thenReturn(Optional.of(new co.piter.buyrecipes.entity.Product("Test Product", 100)));
        when(recipeIngredientRepo.save(any(RecipeIngredient.class))).thenReturn(savedIngredient);

        final RecipeIngredientDto result = recipeIngredientService.createRecipeIngredient(request);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRecipeId()).isEqualTo(1L);
        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getQuantity()).isEqualTo(2);
        assertThat(result.getUnit()).isEqualTo("cups");

        final ArgumentCaptor<RecipeIngredient> ingredientCaptor = ArgumentCaptor.forClass(RecipeIngredient.class);
        verify(recipeIngredientRepo).save(ingredientCaptor.capture());

        final RecipeIngredient capturedIngredient = ingredientCaptor.getValue();
        assertThat(capturedIngredient.getRecipeId()).isEqualTo(1L);
        assertThat(capturedIngredient.getProductId()).isEqualTo(1L);
        assertThat(capturedIngredient.getQuantity()).isEqualTo(2);
        assertThat(capturedIngredient.getUnit()).isEqualTo("cups");
    }

    @Test
    void testCreateRecipeIngredientRecipeNotFound() {
        final CreateRecipeIngredientRequest request = new CreateRecipeIngredientRequest(1L, 1L, 2, "grams");
        when(recipeRepo.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> recipeIngredientService.createRecipeIngredient(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Recipe not found with id: 1");
    }

    @Test
    void testCreateRecipeIngredientProductNotFound() {
        final CreateRecipeIngredientRequest request = new CreateRecipeIngredientRequest(1L, 1L, 2, "tablespoons");
        when(recipeRepo.findById(1L)).thenReturn(Optional.of(new co.piter.buyrecipes.entity.Recipe("Test Recipe", "Test Description")));
        when(productRepo.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> recipeIngredientService.createRecipeIngredient(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Product not found with id: 1");
    }

    @Test
    void testUpdateRecipeIngredient() {
        final RecipeIngredient existingIngredient = new RecipeIngredient(1L, 1L, 2, "grams");
        existingIngredient.setId(1L);
        final RecipeIngredient updatedIngredient = new RecipeIngredient(2L, 2L, 3, "kilograms");
        updatedIngredient.setId(1L);

        final UpdateRecipeIngredientRequest request = new UpdateRecipeIngredientRequest(2L, 2L, 3, "kilograms");

        when(recipeIngredientRepo.findById(1L)).thenReturn(Optional.of(existingIngredient));
        when(recipeRepo.findById(2L)).thenReturn(Optional.of(new co.piter.buyrecipes.entity.Recipe("Test Recipe", "Test Description")));
        when(productRepo.findById(2L)).thenReturn(Optional.of(new co.piter.buyrecipes.entity.Product("Test Product", 100)));
        when(recipeIngredientRepo.save(any(RecipeIngredient.class))).thenReturn(updatedIngredient);

        final Optional<RecipeIngredientDto> result = recipeIngredientService.updateRecipeIngredient(1L, request);
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getRecipeId()).isEqualTo(2L);
        assertThat(result.get().getProductId()).isEqualTo(2L);
        assertThat(result.get().getQuantity()).isEqualTo(3);
        assertThat(result.get().getUnit()).isEqualTo("kilograms");
    }

    @Test
    void testUpdateRecipeIngredientNotFound() {
        final UpdateRecipeIngredientRequest request = new UpdateRecipeIngredientRequest(2L, 2L, 3, "pieces");
        when(recipeIngredientRepo.findById(1L)).thenReturn(Optional.empty());
        assertThat(recipeIngredientService.updateRecipeIngredient(1L, request)).isEmpty();
    }

    @Test
    void testDeleteRecipeIngredient() {
        final RecipeIngredient ingredient = new RecipeIngredient(1L, 1L, 2, "cups");
        ingredient.setId(1L);
        when(recipeIngredientRepo.findById(1L)).thenReturn(Optional.of(ingredient));
        assertThat(recipeIngredientService.deleteRecipeIngredient(1L)).isTrue();
        verify(recipeIngredientRepo).delete(ingredient);
    }

    @Test
    void testDeleteRecipeIngredientNotFound() {
        when(recipeIngredientRepo.findById(1L)).thenReturn(Optional.empty());
        assertThat(recipeIngredientService.deleteRecipeIngredient(1L)).isFalse();
        verify(recipeIngredientRepo, never()).delete(any());
    }

    @Test
    void testCreateRecipeIngredientWithNullUnit() {
        final CreateRecipeIngredientRequest request = new CreateRecipeIngredientRequest(1L, 1L, 2, null);
        final RecipeIngredient savedIngredient = new RecipeIngredient(1L, 1L, 2, null);
        savedIngredient.setId(1L);

        when(recipeRepo.findById(1L)).thenReturn(Optional.of(new co.piter.buyrecipes.entity.Recipe("Test Recipe", "Test Description")));
        when(productRepo.findById(1L)).thenReturn(Optional.of(new co.piter.buyrecipes.entity.Product("Test Product", 100)));
        when(recipeIngredientRepo.save(any(RecipeIngredient.class))).thenReturn(savedIngredient);

        final RecipeIngredientDto result = recipeIngredientService.createRecipeIngredient(request);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRecipeId()).isEqualTo(1L);
        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getQuantity()).isEqualTo(2);
        assertThat(result.getUnit()).isEqualTo("");
    }

    @Test
    void testUpdateRecipeIngredientWithEmptyUnit() {
        final RecipeIngredient existingIngredient = new RecipeIngredient(1L, 1L, 2, "grams");
        existingIngredient.setId(1L);
        final RecipeIngredient updatedIngredient = new RecipeIngredient(1L, 1L, 3, "");
        updatedIngredient.setId(1L);

        final UpdateRecipeIngredientRequest request = new UpdateRecipeIngredientRequest(1L, 1L, 3, "");

        when(recipeIngredientRepo.findById(1L)).thenReturn(Optional.of(existingIngredient));
        when(recipeRepo.findById(1L)).thenReturn(Optional.of(new co.piter.buyrecipes.entity.Recipe("Test Recipe", "Test Description")));
        when(productRepo.findById(1L)).thenReturn(Optional.of(new co.piter.buyrecipes.entity.Product("Test Product", 100)));
        when(recipeIngredientRepo.save(any(RecipeIngredient.class))).thenReturn(updatedIngredient);

        final Optional<RecipeIngredientDto> result = recipeIngredientService.updateRecipeIngredient(1L, request);
        assertThat(result).isPresent();
        assertThat(result.get().getUnit()).isEqualTo("");
    }
}