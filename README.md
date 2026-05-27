# User API - Spring Boot Project

A robust RESTful API built with **Spring Boot** for user management. This project demonstrates modern Java development practices, including layered architecture, data validation, global exception handling, and in-memory persistence.

## 🚀 Technologies

*   **Java 21**: Utilizes modern features like *Records*.
*   **Spring Boot 3.x**: Core framework.
*   **Lombok**: To reduce boilerplate code.
*   **Jakarta Validation**: For DTO and path parameter constraints.
*   **Problem Detail (RFC 7807)**: For standardized API error responses.
*   **In-Memory Storage**: Using Java collections to simulate a database.

## 📋 Features

*   **Full CRUD Logic**: Create, Read (All), and Search (by ID or Name).
*   **Strict Validation**: Email format verification, non-blank fields, and UUID string length constraints.
*   **Global Exception Handling**: Centralized management of errors like `UserNotFound` or `UserAlreadyExists`.
*   **Health Check**: Dedicated endpoint to monitor application status.
*   **Data Mapping**: Custom `UserMapper` to decouple internal models from API responses.

## 📂 Project Structure

```text
src/main/java/cat/itacademy/s04/t01/userapi/
├── UserapiApplication.java          # Main Application Entry Point
├── health_check/                    # Health monitoring components
├── user/
    ├── controller/                  # REST Controllers
    ├── dto/                        # Data Transfer Objects (Records)
    ├── exception/                  # Custom Exceptions & Global Handler
    ├── model/                      # Domain Entities
    ├── repository/                 # Persistence Logic (Interface & In-Memory)
    └── service/                    # Business Logic & Mappers
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
    The server will start at `http://localhost:8080`.

## 🛣️ API Endpoints

### User Management
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **POST** | `/user` | Creates a new user. |
| **GET** | `/users` | Returns a list of all registered users. |
| **GET** | `/user/{id}` | Finds a user by UUID (Id must be between 30-40 chars). |
| **GET** | `/users/search/{name}` | Searches for users by name (case-insensitive). |

### System Status
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **GET** | `/health` | Returns `{"status": "OK"}` if the service is up. |

## 🧪 Usage Examples

### Create a User
**Request (POST `/user`):**
```json
{
    "name": "Jane Doe",
    "email": "jane.doe@example.com"
}
```

### Error Response Example
If you attempt to create a user with an existing email, the API returns a **409 Conflict** status with a standardized body:
```json
{
    "type": "about:blank",
    "title": "User Already Exists",
    "status": 409,
    "detail": "User already exists with email: jane.doe@example.com",
    "instance": "/user"
}
```

## 🛡️ Exception Handling

The application uses a `@ControllerAdvice` to catch and format exceptions:
*   **404 Not Found**: Thrown when a specific ID or Name search yields no results.
*   **409 Conflict**: Thrown when an email is already registered.
*   **400 Bad Request**: Thrown when validation fails (e.g., malformed email or empty fields).

## 📝 Implementation Details
*   **In-Memory Repository**: Uses a `List<User>` to store data. Data is lost when the application stops.
*   **UUID**: Each user is automatically assigned a unique `UUID` upon creation.
*   **Validation**: The `@Validated` annotation on the controller ensures that even path variables (like `{id}`) are checked before the logic executes.

---
*Developed for IT Academy.*