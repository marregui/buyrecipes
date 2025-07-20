# Creating a Paella Recipe with Buy Recipes API

## Prerequisites

- Java 17 or higher
- Git
- curl (for testing API endpoints)

## Step 1: Clone and Setup

1. Clone the repository:

```bash
git clone https://github.com/marregui/buyrecipes.git
cd buyrecipes
```

2. Start the application:

```bash
./gradlew run
```

The API will be available at `http://localhost:8080`

## Step 2: Verify the API is Running

Test that the API is working:

```bash
curl http://localhost:8080/products
```

You should see a JSON response with existing products.

## Step 3: Create Products for Paella

Create all the ingredients needed for a traditional Paella Valenciana:

### 1. Rice (350g)

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Arroz redondo", "priceInCents": 250}'
```

### 2. Chicken (600g)

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Carne de pollo", "priceInCents": 890}'
```

### 3. Rabbit (400g)

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Carne de conejo", "priceInCents": 1200}'
```

### 4. Tomato (1 piece)

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Tomate maduro", "priceInCents": 80}'
```

### 5. Green Beans (150g)

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Bajoqueta (judía verde plana)", "priceInCents": 320}'
```

### 6. Lima Beans (150g)

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Garrofó", "priceInCents": 450}'
```

### 7. Olive Oil (2 tablespoons)

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Aceite de oliva", "priceInCents": 180}'
```

### 8. Sweet Paprika (1 teaspoon)

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Pimentón dulce", "priceInCents": 120}'
```

### 9. Saffron

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Azafrán", "priceInCents": 850}'
```

### 10. Water (3 cups)

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Agua", "priceInCents": 0}'
```

### 11. Salt

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Sal", "priceInCents": 50}'
```

## Step 4: Create the Paella Recipe

Create the recipe:

```bash
curl -X POST http://localhost:8080/recipes \
  -H "Content-Type: application/json" \
  -d '{"name": "Paella Valenciana", "description": "Traditional Spanish paella for 4 people with chicken, rabbit, and vegetables"}'
```

## Step 5: Link Ingredients to the Recipe

### Get all products to find their IDs:

```bash
curl http://localhost:8080/products
```

### Get the recipe ID:

```bash
curl http://localhost:8080/recipes
```

**NOTE!!**: In the following steps, replace `{product_id}` and `{recipe_id}` with the actual IDs from the responses
above.

### Add ingredients to the recipe:

1. **Rice** (350g - quantity: 350):

```bash
curl -X POST http://localhost:8080/recipe-ingredients \
  -H "Content-Type: application/json" \
  -d '{"recipeId": {recipe_id}, "productId": {rice_product_id}, "quantity": 350, "unit": "grams"}'
```

2. **Chicken** (600g - quantity: 600):

```bash
curl -X POST http://localhost:8080/recipe-ingredients \
  -H "Content-Type: application/json" \
  -d '{"recipeId": {recipe_id}, "productId": {chicken_product_id}, "quantity": 600, "unit": "grams"}'
```

3. **Rabbit** (400g - quantity: 400):

```bash
curl -X POST http://localhost:8080/recipe-ingredients \
  -H "Content-Type: application/json" \
  -d '{"recipeId": {recipe_id}, "productId": {rabbit_product_id}, "quantity": 400, "unit": "grams"}'
```

4. **Tomato** (1 piece - quantity: 1):

```bash
curl -X POST http://localhost:8080/recipe-ingredients \
  -H "Content-Type: application/json" \
  -d '{"recipeId": {recipe_id}, "productId": {tomato_product_id}, "quantity": 1, "unit": "pieces"}'
```

5. **Green Beans** (150g - quantity: 150):

```bash
curl -X POST http://localhost:8080/recipe-ingredients \
  -H "Content-Type: application/json" \
  -d '{"recipeId": {recipe_id}, "productId": {green_beans_product_id}, "quantity": 150, "unit": "grams"}'
```

6. **Lima Beans** (150g - quantity: 150):

```bash
curl -X POST http://localhost:8080/recipe-ingredients \
  -H "Content-Type: application/json" \
  -d '{"recipeId": {recipe_id}, "productId": {lima_beans_product_id}, "quantity": 150, "unit": "grams"}'
```

7. **Olive Oil** (2 tablespoons - quantity: 2):

```bash
curl -X POST http://localhost:8080/recipe-ingredients \
  -H "Content-Type: application/json" \
  -d '{"recipeId": {recipe_id}, "productId": {olive_oil_product_id}, "quantity": 2, "unit": "tablespoons"}'
```

8. **Sweet Paprika** (1 teaspoon - quantity: 1):

```bash
curl -X POST http://localhost:8080/recipe-ingredients \
  -H "Content-Type: application/json" \
  -d '{"recipeId": {recipe_id}, "productId": {paprika_product_id}, "quantity": 1, "unit": "teaspoons"}'
```

9. **Saffron** (1 pinch - quantity: 1):

```bash
curl -X POST http://localhost:8080/recipe-ingredients \
  -H "Content-Type: application/json" \
  -d '{"recipeId": {recipe_id}, "productId": {saffron_product_id}, "quantity": 1, "unit": "pinches"}'
```

10. **Water** (3 cups - quantity: 3):

```bash
curl -X POST http://localhost:8080/recipe-ingredients \
  -H "Content-Type: application/json" \
  -d '{"recipeId": {recipe_id}, "productId": {water_product_id}, "quantity": 3, "unit": "cups"}'
```

11. **Salt** (to taste - quantity: 1):

```bash
curl -X POST http://localhost:8080/recipe-ingredients \
  -H "Content-Type: application/json" \
  -d '{"recipeId": {recipe_id}, "productId": {salt_product_id}, "quantity": 1, "unit": "to taste"}'
```

## Step 6: Create a Shopping Cart

Create a new cart:

```bash
curl -X POST http://localhost:8080/carts \
  -H "Content-Type: application/json" \
  -d '{"totalInCents": 0}'
```

## Step 7: Add the Paella Recipe to the Cart

Get the cart ID from the previous response, then add the recipe:

```bash
curl -X POST http://localhost:8080/carts/{cart_id}/add_recipe \
  -H "Content-Type: application/json" \
  -d '{"recipeId": {recipe_id}}'
```

## Step 8: Verify Your Shopping Cart

Check that all ingredients are in your cart:

```bash
curl http://localhost:8080/carts/{cart_id}
```

You should see all 11 ingredients for the Paella Valenciana in your cart!

## Step 9: View Your Complete Recipe

Check your complete recipe with all ingredients:

```bash
curl http://localhost:8080/recipes/{recipe_id}
```

🥘 Congratulations !! 🥘
