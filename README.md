# Library Management API

A simple REST API for managing a library, built with Java and Spring Boot.


## What it does

The API allows you to:

* Create and view authors
* Create and view categories
* Create and view library members
* Create and view books
* Borrow books
* Return books
* Keep track of available book copies

When a book is borrowed, its available copies are automatically decreased. When it is returned, they are increased again.

## Technologies

* Java 17
* Spring Boot
* Spring Data JPA / Hibernate
* PostgreSQL
* Docker
* Gradle
* Swagger / OpenAPI

## Project Structure

The project follows a simple layered structure:

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

## Main Endpoints

```text
GET  /api/authors
POST /api/authors

GET  /api/categories
POST /api/categories

GET  /api/members
POST /api/members

GET  /api/books
POST /api/books

GET  /api/loans
POST /api/loans
PUT  /api/loans/{id}/return
```

## Running the project

You will need:

* Java 17
* Docker

First, start the PostgreSQL container:

```bash
docker compose up -d
```

Then run the Spring Boot application:

```bash
./gradlew bootRun
```

On Windows:

```powershell
.\gradlew.bat bootRun
```

The API will run on:

```text
http://localhost:8080
```

## Swagger

After starting the application, you can use Swagger to view and test the endpoints:

```text
http://localhost:8080/swagger-ui.html
```

## Database

The project uses PostgreSQL running in Docker.

The database configuration is kept outside Git using environment variables, so local credentials are not committed to the repository.


Some things I plan to add as the project develops:

* Better validation
* Centralized error handling
* Update and delete endpoints
* Search and pagination
* Automated tests
* Transaction management
