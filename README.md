# User API - Spring Boot Project

A robust RESTful API built with **Spring Boot** for user management. This project demonstrates modern Java development practices, including layered architecture, data validation, global exception handling, and in-memory persistence.

## 🚀 Technologies

*   **Java 21**: Utilizes modern features like *Records*.
*   **Spring Boot 3.x**: Core framework.
*   **Lombok**: To reduce boilerplate code.
*   **Jakarta Validation**: For DTO and path parameter constraints.
*   **Hibernate Validator (@UUID)**: Specific constraint for valid UUID strings.
*   **Problem Detail (RFC 7807)**: For standardized API error responses.
*   **In-Memory Storage**: Using Java collections to simulate a database.

## 📋 Features

*   **RESTful Design**: Standardized resource naming and proper use of HTTP status codes.
*   **Resource Creation**: Returns `201 Created` with a `Location` header pointing to the new resource.
*   **Dynamic Search**: Dual-purpose endpoint to fetch all users or filter by name using query parameters.
*   **Strict Validation**: Email format verification, non-blank fields, and strict UUID format validation for path variables.
*   **Global Exception Handling**: Centralized management of errors using `@ControllerAdvice`.
*   **Data Mapping**: Decoupled internal models from API responses via a dedicated Mapper.

## 📂 Project Structure

```text
src/main/java/cat/itacademy/s04/t01/userapi/
├── UserapiApplication.java          # Main Application Entry Point
├── health_check/                    # Health monitoring components
├── user/
    ├── controller/                  # REST Controllers (Endpoints definition)
    ├── dto/                         # Data Transfer Objects (Records)
    ├── exception/                   # Custom Exceptions & Global Handler
    ├── model/                       # Domain Entities
    ├── repository/                  # Persistence Logic (Interface & In-Memory)
    └── service/                     # Business Logic & UserMapper
```

## 🛠️ Getting Started

### Prerequisites
*   JDK 21 or higher.
*   Maven 3.x.

### Installation & Execution
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/user-api.git
    cd user-api
    ```

2.  **Run the application:**
    ```bash
    ./mvnw spring-boot:run
    ```
    The server will start at `http://localhost:9000`.

## 🛣️ API Endpoints

All user-related endpoints are prefixed with `/api/users`.

### User Management
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **POST** | `/api/users` | Creates a new user. Returns `Location` header. |
| **GET** | `/api/users` | Returns a list of all registered users. |
| **GET** | `/api/users?name={val}` | Filters users by name (partial match, case-insensitive). |
| **GET** | `/api/users/{id}` | Finds a single user. `{id}` must be a valid UUID. |

### System Status
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **GET** | `/health` | Returns `{"status": "OK"}` if the service is up. |

## 🧪 Usage Examples

### Create a User
**Request (POST `/api/users`):**
```json
{
    "name": "Jane Doe",
    "email": "jane.doe@example.com"
}
```
**Response:** `201 Created`
**Header:** `Location: http://localhost:8080/api/users/550e8400-e29b-41d4-a716-446655440000`

### Search Users by Name
**Request (GET `/api/users?name=jane`):**
Returns all users whose name contains "jane".

### Error Response Example
If you search for a malformed UUID (e.g., `/api/users/123`), the API returns a **400 Bad Request**:
```json
{
    "type": "about:blank",
    "title": "Validation Error in Parameter",
    "status": 400,
    "detail": "getUserById.id: The ID must be a valid UUID",
    "instance": "/api/users/123"
}
```

## 🛡️ Exception Handling

The application uses a `@ControllerAdvice` to catch and format exceptions, using `ProblemDetail`:
*   **404 Not Found**: User not found by ID or no results in name search.
*   **409 Conflict**: Attempting to register an email that already exists.
*   **400 Bad Request**: Validation failures in Request Body or Path Variables (Invalid UUID).

## 📝 Implementation Details
*   **UUID Validation**: Uses `@UUID` from Hibernate Validator on the `@PathVariable` to ensure type safety at the entry point.
*   **Resource Location**: Utilizes `ServletUriComponentsBuilder` to dynamically generate the URL of newly created users.
*   **In-Memory Repository**: Thread-safe operations on Java collections to ensure data consistency during the application lifecycle.

---
*Developed for IT Academy.*