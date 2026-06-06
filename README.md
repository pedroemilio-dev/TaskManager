# TaskManager

A full-stack Task Manager application with a RESTful API built with Spring Boot and a React frontend.

---

# API

A RESTful Task Manager API built with Spring Boot, featuring JWT authentication, project hierarchy with subprojects, and task management.

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Language |
| Spring Boot | 4.0.5 | Framework |
| Spring Security | — | Authentication & Authorization |
| PostgreSQL | — | Database |
| Docker | — | PostgreSQL container |
| Lombok | — | Boilerplate reduction |
| jjwt | — | JWT generation & validation |
| JUnit 5 + Mockito | — | Testing |

---

## Features

- **JWT Authentication** — stateless authentication with access and refresh tokens
- **Token Blacklist** — revoked tokens are stored in the database, preventing reuse after logout
- **Refresh Tokens** — short-lived access tokens (15 min) with long-lived refresh tokens (7 days)
- **Project Hierarchy** — projects can have subprojects (one level deep)
- **Task Management** — tasks can be assigned to projects or kept in inbox
- **Task Priority** — LOW, MEDIUM, HIGH
- **Task Due Dates** — optional due date per task
- **Inbox** — tasks without a project are kept in the inbox

---

## Architecture

The API follows a layered architecture:

```
Controller  →  Service  →  Repository  →  Database
```

- **Controllers** handle HTTP requests and delegate to services
- **Services** contain business logic and throw typed exceptions
- **Repositories** handle database access via Spring Data JPA
- **GlobalExceptionHandler** maps exceptions to semantic HTTP status codes
- **JwtAuthFilter** intercepts every request and validates the JWT before reaching controllers

Authentication is stateless — no server-side sessions. The JWT carries the user identity and is verified on every request using HMAC-SHA256.

---

## Project Structure

```
src/
├── main/java/com/exemplo/taskmanager/
│   ├── config/
│   │   ├── SecurityConfig.java           # Spring Security filter chain
│   │   └── GlobalExceptionHandler.java   # Maps exceptions to HTTP status codes
│   ├── controller/
│   │   ├── AuthController.java           # Register, login, logout, refresh
│   │   ├── ProjectController.java        # Project CRUD
│   │   ├── TaskController.java           # Task CRUD
│   │   └── UserController.java           # Profile management
│   ├── dto/
│   │   ├── auth/                         # Login, register, refresh, response DTOs
│   │   ├── project/                      # Project request and response DTOs
│   │   ├── task/                         # Task request and response DTOs
│   │   └── user/                         # User request and response DTOs
│   ├── exception/
│   │   ├── BusinessRuleException.java    # 400 — business rule violation
│   │   ├── EmailAlreadyExistsException.java # 409 — duplicate email
│   │   ├── InvalidPasswordException.java # 401 — wrong password
│   │   ├── InvalidTokenException.java    # 401 — invalid or revoked token
│   │   └── ResourceNotFoundException.java   # 404 — resource not found
│   ├── model/
│   │   ├── BlacklistedToken.java
│   │   ├── Project.java
│   │   ├── RefreshToken.java
│   │   ├── Task.java
│   │   └── User.java
│   ├── repository/
│   │   ├── BlacklistedTokenRepository.java
│   │   ├── ProjectRepository.java
│   │   ├── RefreshTokenRepository.java
│   │   ├── TaskRepository.java
│   │   └── UserRepository.java
│   ├── security/
│   │   ├── JwtUtil.java                  # JWT generation and validation
│   │   └── SecurityEventLogger.java      # Security event logging (Kafka)
│   ├── service/
│   │   ├── ProjectService.java
│   │   ├── TaskService.java
│   │   ├── UserDetailsServiceImpl.java
│   │   └── UserService.java
│   ├── util/
│   │   └── RequestUtil.java              # Client IP extraction
│   └── JwtAuthFilter.java                # JWT validation filter
└── test/java/com/exemplo/taskmanager/
    ├── controller/
    │   ├── AuthControllerTest.java       # Register, login, refresh, logout
    │   ├── ProjectControllerTest.java    # Project CRUD endpoints
    │   ├── TaskControllerTest.java       # Task CRUD endpoints
    │   └── UserControllerTest.java       # Profile endpoints
    └── service/
        ├── ProjectServiceTest.java
        ├── TaskServiceTest.java
        ├── UserDetailServiceImplTest.java
        └── UserServiceTest.java
```

---

## Testing

The project has two levels of tests:

**Controller tests (`@WebMvcTest`)** — Tests the HTTP layer in isolation. Services are mocked. Verifies status codes, response format, and input validation.

**Service tests (`@ExtendWith(MockitoExtension.class)`)** — Tests business logic in isolation. Repositories are mocked. Verifies exception throwing, data transformations, and business rules.

The following comand runs all the tests:
```bash
mvn test
```

---

## API Endpoints

### Auth — `/api/auth`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | ✗ | Register a new user |
| POST | `/api/auth/login` | ✗ | Login and receive tokens |
| POST | `/api/auth/refresh` | ✗ | Refresh access token |
| POST | `/api/auth/logout` | ✓ | Logout and revoke tokens |

### Users — `/api/users`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/users/me` | ✓ | Get current user profile |
| PATCH | `/api/users/me` | ✓ | Update name or email |
| PATCH | `/api/users/me/password` | ✓ | Change password |
| DELETE | `/api/users/me` | ✓ | Delete account |

### Projects — `/api/projects`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/projects` | ✓ | Get all projects (top-level only) |
| GET | `/api/projects/{id}` | ✓ | Get project by ID |
| POST | `/api/projects` | ✓ | Create a project or subproject |
| PATCH | `/api/projects/{id}` | ✓ | Edit project |
| DELETE | `/api/projects/{id}` | ✓ | Delete project and its tasks |

### Tasks — `/api/tasks`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/tasks/inbox` | ✓ | Get tasks without a project |
| GET | `/api/tasks/{id}` | ✓ | Get task by ID |
| POST | `/api/tasks` | ✓ | Create a task |
| PATCH | `/api/tasks/{id}` | ✓ | Edit task |
| PATCH | `/api/tasks/{id}/toggle` | ✓ | Toggle task completion |
| DELETE | `/api/tasks/{id}` | ✓ | Delete task |

---

## Authentication

All protected endpoints require a Bearer token in the Authorization header:

```
Authorization: Bearer <access_token>
```

Access tokens expire after 15 minutes. Use the refresh token to obtain a new access token without re-authenticating.

---

## Error Codes

| Status | Meaning |
|---|---|
| 400 | Bad request — invalid input or business rule violation |
| 401 | Unauthorized — invalid credentials or token |
| 404 | Not found — resource does not exist |
| 409 | Conflict — resource already exists (e.g. email taken) |
| 500 | Internal server error |

---

# Frontend

*Documentation coming soon.*

---

# Running Locally

## Prerequisites

- Java 21
- Maven
- Docker

## 1. Start PostgreSQL

```bash
docker-compose up -d
```

> This will automatically create the `taskdb` database with the credentials defined in `docker-compose.yml`.

## 2. Configure the API

Copy the example file and set your JWT secret:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Edit `application.properties` and replace `your-secret-key-at-least-32-characters-long` with a real secret.

> Fill in the database credentials to match `docker-compose.yml` (`taskuser` / `123`).
## 3. Run the API

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

## 4. Run the Frontend

*Instructions coming soon.*
