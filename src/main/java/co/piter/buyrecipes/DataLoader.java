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
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;

@Singleton
public class DataLoader implements ApplicationEventListener<ServerStartupEvent> {

    private static final @NotNull Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final @NotNull ProductRepo productRepo;
    private final @NotNull RecipeRepo recipeRepo;
    private final @NotNull RecipeIngredientRepo recipeIngredientRepo;
    private final @NotNull CartRepo cartRepo;

    public DataLoader(
            final @NotNull ProductRepo productRepo,
            final @NotNull RecipeRepo recipeRepo,
            final @NotNull RecipeIngredientRepo recipeIngredientRepo,
            final @NotNull CartRepo cartRepo
    ) {
        this.productRepo = productRepo;
        this.recipeRepo = recipeRepo;
        this.recipeIngredientRepo = recipeIngredientRepo;
        this.cartRepo = cartRepo;
    }

    @Override
    public void onApplicationEvent(final @NotNull ServerStartupEvent event) {
        if (productRepo.count() == 0) {
            log.info("Loading sample data...");
            try {
                final Product flour = productRepo.save(new Product("Flour", 299));
                final Product sugar = productRepo.save(new Product("Sugar", 199));
                final Product eggs = productRepo.save(new Product("Eggs", 349));
                final Product butter = productRepo.save(new Product("Butter", 449));
                final Product chocolateChips = productRepo.save(new Product("Chocolate Chips", 399));
                final Product vanillaExtract = productRepo.save(new Product("Vanilla Extract", 599));
                final Product bakingPowder = productRepo.save(new Product("Baking Powder", 149));
                final Product salt = productRepo.save(new Product("Salt", 99));
                final Product milk = productRepo.save(new Product("Milk", 279));
                final Product tomatoes = productRepo.save(new Product("Tomatoes", 229));
                final Product cheese = productRepo.save(new Product("Cheese", 549));
                final Product pasta = productRepo.save(new Product("Pasta", 159));
                final Product oliveOil = productRepo.save(new Product("Olive Oil", 799));
                final Product garlic = productRepo.save(new Product("Garlic", 89));
                final Product basil = productRepo.save(new Product("Basil", 299));

                final Recipe cookiesRecipe = recipeRepo.save(new Recipe("Chocolate Chip Cookies", "Classic homemade chocolate chip cookies that everyone loves"));
                final Recipe pancakesRecipe = recipeRepo.save(new Recipe("Pancakes", "Fluffy breakfast pancakes perfect for weekend mornings"));
                final Recipe pastaRecipe = recipeRepo.save(new Recipe("Pasta Marinara", "Simple and delicious pasta with tomato sauce"));

                final Long cookiesRecipeId = requireNonNull(cookiesRecipe.getId());
                recipeIngredientRepo.save(new RecipeIngredient(cookiesRecipeId, requireNonNull(flour.getId()), 2, "cups"));
                recipeIngredientRepo.save(new RecipeIngredient(cookiesRecipeId, requireNonNull(sugar.getId()), 1, "cup"));
                recipeIngredientRepo.save(new RecipeIngredient(cookiesRecipeId, requireNonNull(eggs.getId()), 4, "pieces"));
                recipeIngredientRepo.save(new RecipeIngredient(cookiesRecipeId, requireNonNull(butter.getId()), 1, "cup"));
                recipeIngredientRepo.save(new RecipeIngredient(cookiesRecipeId, requireNonNull(chocolateChips.getId()), 12, "ounces"));
                recipeIngredientRepo.save(new RecipeIngredient(cookiesRecipeId, requireNonNull(vanillaExtract.getId()), 2, "teaspoons"));
                recipeIngredientRepo.save(new RecipeIngredient(cookiesRecipeId, requireNonNull(bakingPowder.getId()), 1, "teaspoon"));
                recipeIngredientRepo.save(new RecipeIngredient(cookiesRecipeId, requireNonNull(salt.getId()), 1, "pinch"));

                final Long pancakesRecipeId = requireNonNull(pancakesRecipe.getId());
                recipeIngredientRepo.save(new RecipeIngredient(pancakesRecipeId, requireNonNull(flour.getId()), 1, "cup"));
                recipeIngredientRepo.save(new RecipeIngredient(pancakesRecipeId, requireNonNull(sugar.getId()), 3, "tablespoons"));
                recipeIngredientRepo.save(new RecipeIngredient(pancakesRecipeId, requireNonNull(eggs.getId()), 7, "pieces"));
                recipeIngredientRepo.save(new RecipeIngredient(pancakesRecipeId, requireNonNull(butter.getId()), 2, "tablespoons"));
                recipeIngredientRepo.save(new RecipeIngredient(pancakesRecipeId, requireNonNull(milk.getId()), 1, "cup"));
                recipeIngredientRepo.save(new RecipeIngredient(pancakesRecipeId, requireNonNull(bakingPowder.getId()), 1, "teaspoon"));
                recipeIngredientRepo.save(new RecipeIngredient(pancakesRecipeId, requireNonNull(salt.getId()), 1, "pinch"));

                final Long pastaRecipeId = requireNonNull(pastaRecipe.getId());
                recipeIngredientRepo.save(new RecipeIngredient(pastaRecipeId, requireNonNull(pasta.getId()), 1, "pound"));
                recipeIngredientRepo.save(new RecipeIngredient(pastaRecipeId, requireNonNull(tomatoes.getId()), 8, "pieces"));
                recipeIngredientRepo.save(new RecipeIngredient(pastaRecipeId, requireNonNull(cheese.getId()), 6, "ounces"));
                recipeIngredientRepo.save(new RecipeIngredient(pastaRecipeId, requireNonNull(oliveOil.getId()), 1, "cup"));
                recipeIngredientRepo.save(new RecipeIngredient(pastaRecipeId, requireNonNull(garlic.getId()), 2, "cloves"));
                recipeIngredientRepo.save(new RecipeIngredient(pastaRecipeId, requireNonNull(basil.getId()), 1, "bunch"));
                recipeIngredientRepo.save(new RecipeIngredient(pastaRecipeId, requireNonNull(salt.getId()), 1, "to taste"));

                cartRepo.save(new Cart(0));
                cartRepo.save(new Cart(0));
                log.info("Loaded sample data");
            } catch (final Throwable e) {
                log.error("Error loading sample data", e);
                throw new RuntimeException("Failed to load sample data", e);
            }
        } else {
            log.info("Sample data already exists, skipping data loading...");
        }
    }
}