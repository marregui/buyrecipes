# Buy Recipes API

A REST API for managing recipes, products, and shopping carts with support for both recipe-based and individual product shopping.

## Features

- **Products**: CRUD operations for product management.
- **Recipes**: Create and manage recipes with ingredients and quantities.
- **Recipe Ingredients**: Link products to recipes with specific quantities and units.
- **Shopping Carts**: Flexible cart management supporting:
  - Add/remove entire recipes to carts.
  - Add/remove individual products to carts.  
  - Mixed shopping (recipes + individual products in same cart).
- **Concurrency Control**: Optimistic locking for thread-safe operations.
- **Persistent Storage**: H2 file-backed database with sample data.
- **API Documentation**: Interactive Swagger UI with OpenAPI specification.
- **Docker Support**: Containerized deployment and testing.
- **CI/CD**: GitHub Actions with automated testing and coverage reporting.

## Architecture

- **Framework**: Micronaut 4.9.1
- **Language**: Java 17+
- **Database**: H2 (file-backed)
- **ORM**: Hibernate/JPA
- **Build**: Gradle with Kotlin DSL
- **Testing**: JUnit 5, Mockito, AssertJ, @MicronautTest
- **Containerization**: Docker

## Project Structure

```
src/main/java/co/piter/buyrecipes/
├── rest/     # REST endpoints
├── service/  # Business logic
├── entity/   # JPA entities
├── dto/      # Data transfer objects
└── repo/     # Data access layer
```

## Database

**Connection**: H2 file-backed database

- **JDBC URL**: `jdbc:h2:file:./data/buyrecipes`
- **Username**: `sa`
- **Password**: (empty)

**Sample Data**: Includes 15 products, 3 recipes, and 2 empty carts.

**Persistent**: Data survives application restarts, files are stored in `./data/` and are only accessed by this app. Database files are created automatically on first run and populated with sample data. Hibernate automatically updates schemas when entities change. `DB_CLOSE_ON_EXIT=FALSE` keeps database open after last connection closes.

**Reset Database**: Delete `./data/` directory.

**Optimistic Concurrency Control**: All entities use JPA `@Version` fields to prevent lost updates in concurrent 
scenarios. When multiple transactions attempt to modify the same entity simultaneously, the first commit succeeds 
and subsequent commits throw `OptimisticLockException`, ensuring data integrity without pessimistic locking overhead.

**NOTE**: For schemas and sample queries see [SQL](SQL.md).

## Quick Start

### Run Locally

```bash
./gradlew run
```

API documentation available at `http://localhost:8080` (redirect to `/swagger-ui`).

Swagger UI:
- **Swagger UI**: `http://localhost:8080/swagger-ui`
- **OpenAPI Spec**: `http://localhost:8080/swagger/buy-recipes-api-1.0.yml`

### Run with Docker

```bash
docker compose up -d app
```

## API Reference

For a thorough example, follow the [TUTORIAL](TUTORIAL.md).

### Root

```bash
GET    /                  # Redirects to Swagger UI
```

### Products

```bash
GET    /products          # List all products
GET    /products/{id}     # Get product by ID
POST   /products          # Create product
PUT    /products/{id}     # Update product
DELETE /products/{id}     # Delete product
```

### Recipes

```bash
GET    /recipes           # List all recipes
GET    /recipes/{id}      # Get recipe by ID
GET    /recipes/test      # Test endpoint (returns "Controller is working!")
POST   /recipes           # Create recipe
PUT    /recipes/{id}      # Update recipe
DELETE /recipes/{id}      # Delete recipe
```

### Recipe Ingredients

```bash
GET    /recipe-ingredients                     # List all recipe ingredients
GET    /recipe-ingredients/{id}                # Get recipe ingredient by ID
GET    /recipe-ingredients/recipe/{recipeId}   # Get ingredients for recipe
GET    /recipe-ingredients/product/{productId} # Get recipes using product
POST   /recipe-ingredients                     # Create recipe ingredient
PUT    /recipe-ingredients/{id}                # Update recipe ingredient
DELETE /recipe-ingredients/{id}                # Delete recipe ingredient
```

### Shopping Carts

```bash
GET    /carts                               # List all carts
GET    /carts/{cartId}                      # Get cart by ID
POST   /carts                               # Create cart
PUT    /carts/{cartId}                      # Update cart
DELETE /carts/{cartId}                      # Delete cart
POST   /carts/{cartId}/add_product          # Add individual product to cart
DELETE /carts/{cartId}/products/{productId} # Remove individual product from cart
POST   /carts/{cartId}/add_recipe           # Add recipe to cart (adds all ingredients)
DELETE /carts/{cartId}/recipes/{recipeId}   # Remove recipe from cart (removes all ingredients)
```

### Business Rules

- Recipes must have at least one ingredient
- Adding a recipe to cart creates individual cart items for each ingredient
- Cart totals calculated from all contained products
- All products assumed always available (no inventory)

## Testing

```bash
./gradlew test --tests '*Test' --tests '!*IT'
./gradlew test --tests '*IT'
docker compose --profile test up --build --abort-on-container-exit
```
