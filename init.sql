-- ============================================================
-- init.sql —— 数据库初始化脚本
-- Docker 里的 MySQL 容器【第一次】创建时会自动执行这个文件
-- （依据：docker-compose.yml 里把这个文件挂载到了
--   /docker-entrypoint-initdb.d/ 这个 MySQL 官方镜像约定的目录）
--
-- ⚠️ 只有"第一次"创建容器（数据卷是空的）才会执行
--    如果你已经跑过一次，想重新执行这个脚本，需要先清空数据卷：
--    docker compose down -v   （-v 会把 mysql_data 这个卷也删掉）
--    再重新 docker compose up
-- ============================================================


-- ============================================================
-- 表一：Users —— 用户表
-- 字段来源：registerSQL.xml、updateUsersQL.xml、UsersQL.xml
-- ============================================================
CREATE TABLE IF NOT EXISTS Users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(50) NOT NULL UNIQUE,   -- 登录用户名，registerSQL.xml 里查重用到
    password VARCHAR(255) NOT NULL,          -- 密码（实际项目里应该存加密后的值）
    role VARCHAR(20) DEFAULT 'USER'          -- 角色：USER 普通用户 / ADMIN 管理员，对应 Users.java 里的 role 字段
);


-- ============================================================
-- 表二：products —— 商品表
-- 字段来源：productSQL.xml 的 addProduct、OrderRepository.java 里多处查询
-- ============================================================
CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,            -- 用 DECIMAL 而不是 FLOAT，避免金额精度问题，对应 Java 里的 BigDecimal
    stock_quantity INT NOT NULL DEFAULT 0,   -- 库存，下单/退货时会增减这个字段
    category VARCHAR(50),
    image_url VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',     -- ACTIVE / INACTIVE / OUT_OF_STOCK，对应 ProductAdminDTO.java
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    seller_id BIGINT                         -- 卖家ID，对应 ProductAdminDTO.java 里的 sellerId
);


-- ============================================================
-- 表三：cart_items —— 购物车表
-- 字段来源：cartSQL.xml、CartRepository.java
-- ============================================================
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    selected TINYINT DEFAULT 1,              -- 是否勾选要结算，1=选中 0=未选中，对应 CartItemsDTO.java
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id)
    -- 注：cartSQL.xml 里有 "join products p on c.product_id=p.id"，所以这里建外键约束
);


-- ============================================================
-- 表四：neworders —— 订单主表
-- 字段来源：OrderRepository.java 里的 putOrderRepo / deleteOrderRepo 等方法
-- 注意：表名是 neworders，不是常见的 orders（orders 是 MySQL 保留关键字，容易冲突）
-- ============================================================
CREATE TABLE IF NOT EXISTS neworders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',    -- PENDING / PAID / SHIPPED / COMPLETED / RETURNED，对应代码里的状态流转逻辑
    deliver_date DATE,                       -- 预计送达日期，putOrderRepo 里用 date_add(now(), interval 7 day) 生成
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


-- ============================================================
-- 表五：neworder_detail —— 订单明细表（一个订单可能包含多件商品）
-- 字段来源：OrderRepository.java 里的 searchOrderRepo（联查 neworders 和这张表）
-- ============================================================
CREATE TABLE IF NOT EXISTS neworder_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    product_name VARCHAR(100),               -- 冗余存一份商品名/价格快照，避免商品改价后历史订单数据跟着变
    price DECIMAL(10,2),
    image_url VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES neworders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);


-- ============================================================
-- 测试数据（种子数据）—— 方便容器一启动就有东西可以看
-- 不想要的话可以把下面这些 INSERT 删掉，表还是会正常建好，只是是空的
-- ============================================================

-- 测试商品
INSERT INTO products (product_name, description, price, stock_quantity, category, image_url, status, seller_id) VALUES
('苹果 iPhone 15', '128GB 黑色', 5999.00, 50, '电子产品', 'https://example.com/iphone15.jpg', 'ACTIVE', 1),
('耐克运动鞋', '舒适透气跑步鞋', 599.00, 100, '服装', 'https://example.com/nike.jpg', 'ACTIVE', 1),
('有机苹果', '新鲜红富士苹果 5斤装', 29.90, 200, '食品', 'https://example.com/apple.jpg', 'ACTIVE', 2);

-- 测试用户（密码这里是明文仅作演示，实际项目应存加密后的密码，请用注册接口创建真实测试账号）
INSERT INTO Users (user_name, password, role) VALUES
('testuser', 'test123', 'USER'),
('admin', 'admin123', 'ADMIN');
