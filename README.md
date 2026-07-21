# Online Store

A full-stack e-commerce backend built with Spring Boot, featuring JWT-based authentication, shopping cart management, and order processing. Built as a personal project to practice REST API design, Spring Security, and containerized deployment.

## Tech Stack

- **Language:** Java 17
- **Framework:** Spring Boot 3.5, Spring Security, Spring MVC
- **Persistence:** MyBatis, Spring JDBC (JdbcTemplate), MySQL 8
- **Caching:** Redis
- **Auth:** JWT (JJWT), BCrypt password hashing
- **API Docs:** springdoc-openapi (Swagger UI)
- **Testing:** JUnit 5, Mockito
- **Deployment:** Docker, Docker Compose

## Features

- **User accounts** — registration, login, JWT-based stateless authentication, role-based access (USER / ADMIN)
- **Product catalog** — browse and search products
- **Shopping cart** — add, update, delete, and view cart items
- **Orders** — place orders, amend pending orders, cancel/return orders, admin-controlled status transitions (PENDING → PAID → SHIPPED → COMPLETED)
- **Inventory management** — stock levels automatically adjusted on order placement, cancellation, and return

## Architecture

The project follows a layered architecture:

```
Controller  ->  Service  ->  Repository / Mapper  ->  MySQL
                                  ^
                          MyBatis XML mappers (parameterized SQL)
```

- `security/` — JWT generation/validation, Spring Security configuration, BCrypt password encoding
- `controller/` — REST endpoints
- `service/` — business logic
- `repository/` + `mapper/` — data access (JdbcTemplate + MyBatis)
- `model/` — request/response DTOs

## Getting Started

### Prerequisites
- Java 17
- Docker & Docker Compose

### Run locally with Docker

```bash
git clone https://github.com/TinaFruit/online-store.git
cd online-store
cp .env.example .env   # fill in your own DB credentials
docker compose up
```

The app will be available at `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui.html`

### Environment variables

This project reads database and cache configuration from environment variables (see `application.yaml`). Set these in a local `.env` file (not committed to git):

| Variable | Description | Default |
|---|---|---|
| `DB_HOST` | MySQL host | `localhost` |
| `DB_PORT` | MySQL port | `3306` |
| `DB_NAME` | Database name | `test` |
| `DB_USERNAME` | MySQL username | `root` |
| `DB_PASSWORD` | MySQL password | — (required, no default) |
| `REDIS_HOST` | Redis host | `localhost` |
| `REDIS_PORT` | Redis port | `6379` |

## API Overview

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/register` | Create a new user account | Public |
| POST | `/login` | Authenticate and receive a JWT | Public |
| GET | `/cart/searchAll` | View the current user's cart | User |
| POST | `/cart/add` | Add an item to cart | User |
| PUT | `/cart/update` | Update cart item quantity | User |
| DELETE | `/cart/delete` | Remove a cart item | User |
| POST | `/order/putOrder` | Place an order | User |
| GET | `/order/searchOrder` | View the current user's orders | User |
| PUT | `/order/admendent/{orderId}` | Amend a pending order | User |
| PUT | `/order/status/{orderId}` | Update order status | Admin |
| POST | `/order/return/{orderId}` | Process a return | Admin |
| DELETE | `/order/delete/{id}` | Delete an order | Admin |

## Roadmap / Known Improvements

This is an actively evolving learning project. Planned improvements include:
- Tightening object-level authorization on cart/order endpoints (ensuring users can only access their own resources)
- Moving all secrets fully out of source control via `.env` + secret manager
- Adding integration tests for the order and cart flows

## License

Personal/educational project — not licensed for production use.# online-store-MongoDB
