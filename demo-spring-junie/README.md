# Spring Boot Junie

A sample Spring Boot application demonstrating best practices and guidelines for building robust, maintainable Spring Boot applications.

## Features

- RESTful API for managing products
- Layered architecture (Controller, Service, Repository)
- Proper transaction boundaries
- Validation of input data
- Pagination for collection resources
- Exception handling with consistent error responses
- Comprehensive test coverage

## Architecture

The application follows a standard layered architecture:

- **Controller Layer**: Handles HTTP requests and responses
- **Service Layer**: Contains business logic and transaction boundaries
- **Repository Layer**: Provides data access
- **Domain Layer**: Contains the domain model (entities)
- **DTO Layer**: Contains Data Transfer Objects for request/response

## Technologies

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database (in-memory)
- Lombok
- JUnit 5
- AssertJ
- Mockito

## API Endpoints

The application exposes the following RESTful endpoints:

- `GET /api/v1/products`: Get all products (paginated)
- `GET /api/v1/products/{id}`: Get a product by ID
- `GET /api/v1/products/search?query={text}`: Search products by text
- `GET /api/v1/products/by-price?maxPrice={price}`: Find products by maximum price
- `POST /api/v1/products`: Create a new product
- `PUT /api/v1/products/{id}`: Update an existing product
- `DELETE /api/v1/products/{id}`: Delete a product

## Running the Application

### Prerequisites

- Java 17 or higher
- Maven

### Steps

1. Clone the repository
2. Navigate to the project directory
3. Run the application using Maven:

```bash
mvn spring-boot:run
```

The application will start on port 8080 by default.

## Testing

The application includes various types of tests:

- Unit tests for service and controller layers
- Repository tests using @DataJpaTest
- Integration tests using @SpringBootTest

To run the tests:

```bash
mvn test
```

## Configuration

The application can be configured through the `application.properties` file. Key configuration properties include:

- `app.junie.feature.enabled`: Enable/disable certain features
- `app.junie.max-items-per-page`: Maximum number of items per page in paginated responses

## Best Practices Implemented

- Constructor injection for dependencies
- Package-private visibility for Spring components
- Typed configuration properties
- Clear transaction boundaries
- Separation of web and persistence layers
- REST API design principles
- Centralized exception handling
- Proper logging
- Comprehensive testing