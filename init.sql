-- ============================================================
-- init.sql -- Database initialization script
-- Automatically executed by the MySQL container on its FIRST creation
-- (mounted to /docker-entrypoint-initdb.d/, the MySQL image's
--  convention directory, via docker-compose.yml)
--
-- Note: this only runs the FIRST time the container is created
-- (i.e., when the data volume is empty). If you've already run it once
-- and want to re-run it, clear the data volume first:
--   docker compose down -v   (-v also removes the mysql_data volume)
--   then run docker compose up again
-- ============================================================


-- ============================================================
-- Table 1: Users
-- Referenced by: registerSQL.xml, updateUsersQL.xml, UsersQL.xml
-- ============================================================
CREATE TABLE IF NOT EXISTS Users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(50) NOT NULL UNIQUE,   -- login username; used for uniqueness checks in registerSQL.xml
    password VARCHAR(255) NOT NULL,          -- password (should be stored hashed in a real project)
    role VARCHAR(20) DEFAULT 'USER'          -- role: USER (regular user) / ADMIN, maps to the role field in Users.java
);


-- ============================================================
-- Table 2: products
-- Referenced by: productSQL.xml (addProduct), multiple queries in OrderRepository.java
-- ============================================================
CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,            -- DECIMAL instead of FLOAT to avoid rounding errors; maps to BigDecimal in Java
    stock_quantity INT NOT NULL DEFAULT 0,   -- stock level; increased/decreased on order placement and returns
    category VARCHAR(50),
    image_url VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',     -- ACTIVE / INACTIVE / OUT_OF_STOCK, maps to ProductAdminDTO.java
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    seller_id BIGINT                         -- seller ID, maps to sellerId in ProductAdminDTO.java
);


-- ============================================================
-- Table 3: cart_items
-- Referenced by: cartSQL.xml, CartRepository.java
-- ============================================================
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    selected TINYINT DEFAULT 1,              -- whether selected for checkout: 1 = selected, 0 = not selected; maps to CartItemsDTO.java
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id)
    -- note: cartSQL.xml joins "products p on c.product_id=p.id", hence this FK constraint
);


-- ============================================================
-- Table 4: neworders (main orders table)
-- Referenced by: putOrderRepo / deleteOrderRepo etc. in OrderRepository.java
-- Note: named "neworders" rather than "orders" since ORDER is a reserved MySQL keyword
-- ============================================================
CREATE TABLE IF NOT EXISTS neworders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',    -- PENDING / PAID / SHIPPED / COMPLETED / RETURNED, drives the status transition logic in code
    deliver_date DATE,                       -- estimated delivery date; set via date_add(now(), interval 7 day) in putOrderRepo
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


-- ============================================================
-- Table 5: neworder_detail (order line items -- one order can contain multiple products)
-- Referenced by: searchOrderRepo in OrderRepository.java (joins neworders and this table)
-- ============================================================
CREATE TABLE IF NOT EXISTS neworder_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    product_name VARCHAR(100),               -- denormalized snapshot of product name/price, so historical orders
    price DECIMAL(10,2),                     -- stay accurate even if the product's price changes later
    image_url VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES neworders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);


-- ============================================================
-- Seed data -- so the container has some data to look at right after startup
-- Feel free to remove these INSERTs; the tables will still be created, just empty
-- ============================================================

-- Sample products
INSERT INTO products (product_name, description, price, stock_quantity, category, image_url, status, seller_id) VALUES
('Apple iPhone 15', '128GB, Black', 5999.00, 50, 'Electronics', 'https://example.com/iphone15.jpg', 'ACTIVE', 1),
('Nike Running Shoes', 'Comfortable and breathable', 599.00, 100, 'Apparel', 'https://example.com/nike.jpg', 'ACTIVE', 1),
('Organic Apples', 'Fresh Fuji apples, 5 lb bag', 29.90, 200, 'Grocery', 'https://example.com/apple.jpg', 'ACTIVE', 2);

-- Sample users (passwords here are plaintext for demo purposes only --
-- a real project should store hashed passwords; use the /register endpoint to create real test accounts)
INSERT INTO Users (user_name, password, role) VALUES
('testuser', 'test123', 'USER'),
('admin', 'admin123', 'ADMIN');