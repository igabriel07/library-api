# Library Management API

A REST API for managing a library, built with Java and Spring Boot.

## What it does

The API allows you to:

* Create and view authors
* Create and view categories
* Create and view library members
* Create, view, update and delete books
* Create and view loans
* Return borrowed books
* Keep track of available book copies
* Validate incoming request data
* Handle API errors with centralized exception handling

When a book is borrowed, its available copies are automatically decreased. When it is returned, they are increased again.

## Technologies

* Java 17
* Spring Boot
* Spring Data JPA / Hibernate
* PostgreSQL
* Docker
* Gradle
* Swagger / OpenAPI
* JUnit
* Mockito

## Project Structure

The project follows a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

DTOs are used for the data sent to and returned from the API.

The project also includes centralized exception handling for validation errors, missing resources and business rule violations.

## Main Endpoints

### Authors

```text
GET  /api/authors
POST /api/authors
```

### Categories

```text
GET  /api/categories
POST /api/categories
```

### Members

```text
GET  /api/members
POST /api/members
```

### Books

```text
GET    /api/books
GET    /api/books/{id}
POST   /api/books
PUT    /api/books/{id}
DELETE /api/books/{id}
```

### Loans

```text
GET /api/loans
GET /api/loans/{id}
POST /api/loans
PUT /api/loans/{id}/return
```

## Business Rules

The API enforces several business rules, including:

* Available book copies cannot be greater than total copies
* A loan cannot be created when no book copies are available
* The due date cannot be before the loan date
* A book, author, category or member must exist before it can be referenced
* A loan cannot be returned more than once
* Returning a book automatically increases its available copies
* Borrowing a book automatically decreases its available copies

## Validation and Error Handling

Request DTOs use Jakarta Bean Validation to validate incoming data.

The API provides centralized exception handling for common errors, including:

* `400 Bad Request` for validation and business rule violations
* `404 Not Found` for missing resources
* Appropriate HTTP status codes for successful operations

## Automated Tests

The project includes automated tests using JUnit and Mockito.

The service and controller layers are tested for successful operations, validation, business rules and error scenarios.

The test suite currently covers:

* Book creation, update and deletion
* Book controller endpoints
* Missing books, authors and categories
* Invalid book copy counts
* Loan creation and return
* Loan controller endpoints
* Unavailable books
* Invalid loan dates
* Missing members and books
* Returning an already returned loan
* Request validation
* API error responses

The full test suite currently contains **44 automated tests**.

Run all tests with:

```powershell
.\gradlew test
```

## Running the Project

You will need:

* Java 17
* Docker

First, start the PostgreSQL container:

```bash
docker compose up -d
```

Then run the Spring Boot application.

On Windows:

```powershell
.\gradlew bootRun
```

On Linux/macOS:

```bash
./gradlew bootRun
```

The API will run on:

```text
http://localhost:8081
```

## Swagger

After starting the application, Swagger UI is available at:

```text
http://localhost:8081/swagger-ui/index.html
```

Swagger can be used to view and test the available API endpoints.

## Database

The project uses PostgreSQL running in Docker.

Database credentials are stored in environment variables and are not committed to the repository.

The PostgreSQL container uses host port `5433` to avoid conflicts with a local PostgreSQL installation.

The test environment uses a separate test configuration to connect to the PostgreSQL database when running the Spring application context tests.

## Testing

Run all automated tests with:

```powershell
.\gradlew test
```

To run a specific test class:

```powershell
.\gradlew test --tests com.library.service.BookServiceTest
```

## Build

To build the project:

```powershell
.\gradlew build
```

## Future Improvements

Possible future improvements include:

* Search by title, ISBN or other book attributes
* Pagination and sorting
* More advanced filtering
* Improved API documentation
* Dockerizing the Spring Boot application
* Additional integration tests
* Transaction management
* Database indexes and further database optimization
