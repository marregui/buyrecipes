package co.piter.buyrecipes.service;

import co.piter.buyrecipes.dto.CreateRecipeRequest;
import co.piter.buyrecipes.dto.ProductDto;
import co.piter.buyrecipes.dto.RecipeDto;
import co.piter.buyrecipes.dto.UpdateRecipeRequest;
import co.piter.buyrecipes.entity.Product;
import co.piter.buyrecipes.entity.Recipe;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RecipeServiceTest {

    @Mock
    private RecipeRepo recipeRepo;

    @Mock
    private RecipeIngredientRepo recipeIngredientRepo;

    @Mock
    private ProductRepo productRepo;

    private RecipeService recipeService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        recipeService = new RecipeService(recipeRepo, recipeIngredientRepo, productRepo);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getAllRecipes_ShouldReturnRecipesWithIngredients() {
        final Recipe recipe1 = new Recipe("Chocolate Chip Cookies", "Classic cookies");
        recipe1.setId(1L);
        final Recipe recipe2 = new Recipe("Pancakes", "Fluffy pancakes");
        recipe2.setId(2L);
        final List<Recipe> recipes = List.of(recipe1, recipe2);

        final RecipeIngredient ingredient1 = new RecipeIngredient(1L, 1L, 2);
        final RecipeIngredient ingredient2 = new RecipeIngredient(1L, 2L, 1);
        final List<RecipeIngredient> recipe1Ingredients = List.of(ingredient1, ingredient2);

        final RecipeIngredient ingredient3 = new RecipeIngredient(2L, 1L, 1);
        final List<RecipeIngredient> recipe2Ingredients = List.of(ingredient3);

        final Product flour = new Product("Flour", 299);
        flour.setId(1L);
        final Product sugar = new Product("Sugar", 199);
        sugar.setId(2L);

        when(recipeRepo.findAll()).thenReturn(recipes);
        when(recipeIngredientRepo.findByRecipeId(1L)).thenReturn(recipe1Ingredients);
        when(recipeIngredientRepo.findByRecipeId(2L)).thenReturn(recipe2Ingredients);
        when(productRepo.findById(1L)).thenReturn(Optional.of(flour));
        when(productRepo.findById(2L)).thenReturn(Optional.of(sugar));

        final List<RecipeDto> result = recipeService.getAllRecipes();
        assertThat(result).hasSize(2);

        final RecipeDto firstRecipe = result.get(0);
        assertThat(firstRecipe.getId()).isEqualTo(1L);
        assertThat(firstRecipe.getName()).isEqualTo("Chocolate Chip Cookies");
        assertThat(firstRecipe.getDescription()).isEqualTo("Classic cookies");
        assertThat(firstRecipe.getIngredients()).hasSize(2);

        final ProductDto firstIngredient = firstRecipe.getIngredients().get(0);
        assertThat(firstIngredient.getId()).isEqualTo(1L);
        assertThat(firstIngredient.getName()).isEqualTo("Flour");
        assertThat(firstIngredient.getPriceInCents()).isEqualTo(299);
        assertThat(firstIngredient.getQuantity()).isEqualTo(2);

        final ProductDto secondIngredient = firstRecipe.getIngredients().get(1);
        assertThat(secondIngredient.getId()).isEqualTo(2L);
        assertThat(secondIngredient.getName()).isEqualTo("Sugar");
        assertThat(secondIngredient.getPriceInCents()).isEqualTo(199);
        assertThat(secondIngredient.getQuantity()).isEqualTo(1);

        final RecipeDto secondRecipe = result.get(1);
        assertThat(secondRecipe.getId()).isEqualTo(2L);
        assertThat(secondRecipe.getName()).isEqualTo("Pancakes");
        assertThat(secondRecipe.getDescription()).isEqualTo("Fluffy pancakes");
        assertThat(secondRecipe.getIngredients()).hasSize(1);
    }

    @Test
    void getRecipeById_WhenRecipeExists_ShouldReturnRecipeWithIngredients() {
        final Recipe recipe = new Recipe("Chocolate Chip Cookies", "Classic cookies");
        recipe.setId(1L);

        final RecipeIngredient ingredient = new RecipeIngredient(1L, 1L, 2);
        final List<RecipeIngredient> ingredients = List.of(ingredient);

        final Product flour = new Product("Flour", 299);
        flour.setId(1L);

        when(recipeRepo.findById(1L)).thenReturn(Optional.of(recipe));
        when(recipeIngredientRepo.findByRecipeId(1L)).thenReturn(ingredients);
        when(productRepo.findById(1L)).thenReturn(Optional.of(flour));

        final Optional<RecipeDto> result = recipeService.getRecipeById(1L);
        assertThat(result).isPresent();

        final RecipeDto recipeDto = result.get();
        assertThat(recipeDto.getId()).isEqualTo(1L);
        assertThat(recipeDto.getName()).isEqualTo("Chocolate Chip Cookies");
        assertThat(recipeDto.getDescription()).isEqualTo("Classic cookies");
        assertThat(recipeDto.getIngredients()).hasSize(1);

        final ProductDto ingredientDto = recipeDto.getIngredients().get(0);
        assertThat(ingredientDto.getId()).isEqualTo(1L);
        assertThat(ingredientDto.getName()).isEqualTo("Flour");
        assertThat(ingredientDto.getPriceInCents()).isEqualTo(299);
        assertThat(ingredientDto.getQuantity()).isEqualTo(2);
    }

    @Test
    void getRecipeById_WhenRecipeNotFound_ShouldReturnEmpty() {
        when(recipeRepo.findById(999L)).thenReturn(Optional.empty());
        assertThat(recipeService.getRecipeById(999L)).isEmpty();
    }

    @Test
    void getRecipeIngredients_ShouldReturnCorrectIngredients() {
        final RecipeIngredient ingredient1 = new RecipeIngredient(1L, 1L, 2);
        final RecipeIngredient ingredient2 = new RecipeIngredient(1L, 2L, 1);
        final List<RecipeIngredient> ingredients = List.of(ingredient1, ingredient2);

        final Product flour = new Product("Flour", 299);
        flour.setId(1L);
        final Product sugar = new Product("Sugar", 199);
        sugar.setId(2L);

        when(recipeIngredientRepo.findByRecipeId(1L)).thenReturn(ingredients);
        when(productRepo.findById(1L)).thenReturn(Optional.of(flour));
        when(productRepo.findById(2L)).thenReturn(Optional.of(sugar));

        final List<ProductDto> result = recipeService.getRecipeIngredients(1L);
        assertThat(result).hasSize(2);

        final ProductDto firstIngredient = result.get(0);
        assertThat(firstIngredient.getId()).isEqualTo(1L);
        assertThat(firstIngredient.getName()).isEqualTo("Flour");
        assertThat(firstIngredient.getPriceInCents()).isEqualTo(299);
        assertThat(firstIngredient.getQuantity()).isEqualTo(2);

        final ProductDto secondIngredient = result.get(1);
        assertThat(secondIngredient.getId()).isEqualTo(2L);
        assertThat(secondIngredient.getName()).isEqualTo("Sugar");
        assertThat(secondIngredient.getPriceInCents()).isEqualTo(199);
        assertThat(secondIngredient.getQuantity()).isEqualTo(1);
    }

    @Test
    void testCreateRecipe() {
        final CreateRecipeRequest request = new CreateRecipeRequest("Test Recipe", "Test Description");
        final Recipe savedRecipe = new Recipe("Test Recipe", "Test Description");
        savedRecipe.setId(1L);

        when(recipeRepo.save(any(Recipe.class))).thenReturn(savedRecipe);
        when(recipeIngredientRepo.findByRecipeId(1L)).thenReturn(List.of());

        final RecipeDto result = recipeService.createRecipe(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Recipe");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getIngredients()).isEmpty();

        final ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeRepo).save(recipeCaptor.capture());

        final Recipe capturedRecipe = recipeCaptor.getValue();
        assertThat(capturedRecipe.getName()).isEqualTo("Test Recipe");
        assertThat(capturedRecipe.getDescription()).isEqualTo("Test Description");
    }

    @Test
    void testUpdateRecipe() {
        final Recipe existingRecipe = new Recipe("Old Name", "Old Description");
        existingRecipe.setId(1L);

        final Recipe updatedRecipe = new Recipe("New Name", "New Description");
        updatedRecipe.setId(1L);

        final UpdateRecipeRequest request = new UpdateRecipeRequest("New Name", "New Description");

        when(recipeRepo.findById(1L)).thenReturn(Optional.of(existingRecipe));
        when(recipeRepo.save(any(Recipe.class))).thenReturn(updatedRecipe);
        when(recipeIngredientRepo.findByRecipeId(1L)).thenReturn(List.of());

        final Optional<RecipeDto> result = recipeService.updateRecipe(1L, request);
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("New Name");
        assertThat(result.get().getDescription()).isEqualTo("New Description");
    }

    @Test
    void testUpdateRecipeNotFound() {
        final UpdateRecipeRequest request = new UpdateRecipeRequest("New Name", "New Description");
        when(recipeRepo.findById(1L)).thenReturn(Optional.empty());
        assertThat(recipeService.updateRecipe(1L, request)).isEmpty();
    }

    @Test
    void testDeleteRecipe() {
        final Recipe recipe = new Recipe("Test Recipe", "Test Description");
        recipe.setId(1L);
        when(recipeRepo.findById(1L)).thenReturn(Optional.of(recipe));
        assertThat(recipeService.deleteRecipe(1L)).isTrue();
        verify(recipeIngredientRepo).deleteByRecipeId(1L);
        verify(recipeRepo).delete(recipe);
    }

    @Test
    void testDeleteRecipeNotFound() {
        when(recipeRepo.findById(1L)).thenReturn(Optional.empty());
        assertThat(recipeService.deleteRecipe(1L)).isFalse();
        verify(recipeIngredientRepo, never()).deleteByRecipeId(1L);
        verify(recipeRepo, never()).delete(any());
    }
}