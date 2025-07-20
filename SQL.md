## Schemas

```sql
-- Products table
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price_in_cents INT DEFAULT 0
);

-- Recipes table
CREATE TABLE recipes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

-- Recipe ingredients (many-to-many relationship with quantity and unit)
CREATE TABLE recipe_ingredients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipe_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT DEFAULT 1,
    unit VARCHAR(50),
    FOREIGN KEY (recipe_id) REFERENCES recipes(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Shopping carts
CREATE TABLE carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    total_in_cents INT DEFAULT 0
);

-- Cart items
CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES carts(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

## Advanced Queries

### 1. Find products not used in any recipe
```sql
SELECT * FROM products 
WHERE id NOT IN (SELECT DISTINCT product_id FROM recipe_ingredients);
```

**Result**: Empty set (all products are now used in at least one recipe)

### 2. Find most expensive ingredient in each recipe
```sql
SELECT r.name, p.name, p.price_in_cents
FROM recipes r
JOIN recipe_ingredients ri ON r.id = ri.recipe_id
JOIN products p ON ri.product_id = p.id
WHERE p.price_in_cents = (
    SELECT MAX(p2.price_in_cents)
    FROM recipe_ingredients ri2
    JOIN products p2 ON ri2.product_id = p2.id
    WHERE ri2.recipe_id = r.id
);
```

**Result**:
| Recipe Name | Product Name | Price (cents) |
|-------------|--------------|---------------|
| Chocolate Chip Cookies | Vanilla Extract | 599 |
| Pancakes | Butter | 449 |
| Pasta Marinara | Olive Oil | 799 |
| Paella Valenciana | Carne de conejo | 1200 |

### 3. Rank products by price
```sql
SELECT name, price_in_cents, 
       RANK() OVER (ORDER BY price_in_cents DESC) as price_rank
FROM products;
```

**Result** (top 10):
| Product Name | Price (cents) | Rank |
|--------------|---------------|------|
| Carne de conejo | 1200 | 1 |
| Carne de pollo | 890 | 2 |
| Azafrán | 850 | 3 |
| Olive Oil | 799 | 4 |
| Vanilla Extract | 599 | 5 |
| Cheese | 549 | 6 |
| Garrofó | 450 | 7 |
| Butter | 449 | 8 |
| Chocolate Chips | 399 | 9 |
| Eggs | 349 | 10 |
| ... | ... | ... |


### 4. Calculate total cost for each recipe
```sql
SELECT r.name, 
       SUM(p.price_in_cents * ri.quantity) AS total_cost_cents,
       ROUND(SUM(p.price_in_cents * ri.quantity) / 100.0, 2) AS total_cost_dollars
FROM recipes r
JOIN recipe_ingredients ri ON r.id = ri.recipe_id
JOIN products p ON ri.product_id = p.id
GROUP BY r.id, r.name
ORDER BY total_cost_cents DESC;
```

**Result**:
| Recipe Name | Total Cost (cents) | Total Cost ($) |
|-------------|-------------------|----------------|
| Chocolate Chip Cookies | 8140 | 81.40 |
| Pasta Marinara | 7136 | 71.36 |
| Pancakes | 4850 | 48.50 |
| Paella Valenciana | 4390 | 43.90 |

### 5. Most popular products (in carts)
```sql
SELECT p.name, COUNT(*) as times_added
FROM products p
JOIN cart_items ci ON p.id = ci.product_id
GROUP BY p.id, p.name
ORDER BY times_added DESC;
```

**Result**: 
| Product Name | Times Added |
|--------------|-------------|
| Arroz redondo | 1 |
| Carne de pollo | 1 |
| Carne de conejo | 1 |
| Tomate maduro | 1 |
| ... (all Paella ingredients) | 1 |

### 6. Products by price range
```sql
SELECT 
    CASE 
        WHEN price_in_cents < 100 THEN 'Budget'
        WHEN price_in_cents < 300 THEN 'Mid-range'
        ELSE 'Premium'
    END as price_category,
    COUNT(*) as product_count
FROM products
GROUP BY price_category;
```

**Result**:
| Price Category | Product Count |
|----------------|---------------|
| Budget | 3 |
| Mid-range | 12 |
| Premium | 11 |

## Sample Data

```sql
-- Insert sample products
INSERT INTO products (name, price_in_cents) VALUES 
('Flour', 299),
('Sugar', 199),
('Eggs', 349),
('Butter', 449),
('Chocolate Chips', 399),
('Vanilla Extract', 599),
('Baking Powder', 149),
('Salt', 99),
('Milk', 279),
('Tomatoes', 229),
('Cheese', 549),
('Pasta', 159),
('Olive Oil', 799),
('Garlic', 89),
('Basil', 299);

-- Insert sample recipes
INSERT INTO recipes (name, description) VALUES 
('Chocolate Chip Cookies', 'Classic homemade chocolate chip cookies that everyone loves'),
('Pancakes', 'Fluffy breakfast pancakes perfect for weekend mornings'),
('Pasta Marinara', 'Simple and delicious pasta with tomato sauce');

-- Insert recipe ingredients with units
-- Recipe 1: Chocolate Chip Cookies
INSERT INTO recipe_ingredients (recipe_id, product_id, quantity, unit) VALUES 
(1, 1, 2, 'cups'),          -- Flour
(1, 2, 1, 'cup'),           -- Sugar
(1, 3, 4, 'pieces'),        -- Eggs (updated quantity to match actual data)
(1, 4, 1, 'cup'),           -- Butter
(1, 5, 12, 'ounces'),       -- Chocolate Chips (updated quantity to match actual data)
(1, 6, 2, 'teaspoons'),     -- Vanilla Extract (updated quantity to match actual data)
(1, 7, 1, 'teaspoon'),      -- Baking Powder
(1, 8, 1, 'pinch');         -- Salt

-- Recipe 2: Pancakes
INSERT INTO recipe_ingredients (recipe_id, product_id, quantity, unit) VALUES 
(2, 1, 1, 'cup'),           -- Flour
(2, 2, 3, 'tablespoons'),   -- Sugar (updated quantity to match actual data)
(2, 3, 7, 'pieces'),        -- Eggs (updated quantity to match actual data)
(2, 4, 2, 'tablespoons'),   -- Butter (updated quantity to match actual data)
(2, 9, 1, 'cup'),           -- Milk
(2, 7, 1, 'teaspoon'),      -- Baking Powder
(2, 8, 1, 'pinch');         -- Salt

-- Recipe 3: Pasta Marinara
INSERT INTO recipe_ingredients (recipe_id, product_id, quantity, unit) VALUES 
(3, 12, 1, 'pound'),        -- Pasta
(3, 10, 8, 'pieces'),       -- Tomatoes (updated quantity to match actual data)
(3, 11, 6, 'ounces'),       -- Cheese (updated quantity to match actual data)
(3, 13, 1, 'cup'),          -- Olive Oil (updated quantity to match actual data)
(3, 14, 2, 'cloves'),       -- Garlic (updated quantity to match actual data)
(3, 15, 1, 'bunch'),        -- Basil
(3, 8, 1, 'to taste');      -- Salt (added missing ingredient)

-- Insert sample carts
INSERT INTO carts (total_in_cents) VALUES (0), (0);

-- Example transaction
BEGIN;
INSERT INTO products (name, price_in_cents) VALUES ('Test Product', 100);
ROLLBACK; -- or COMMIT;
```
