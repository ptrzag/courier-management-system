# Courier Management System

RESTful backend application for managing couriers, parcels, vehicles, and delivery routes. Built with Java 21 and Spring Boot 3.1.

## Tech Stack

- **Java 21** + **Spring Boot 3.1**
- **Spring Data JPA** — data persistence (ORM)
- **Spring HATEOAS** — hypermedia-driven REST API
- **Spring Validation** — input validation with Bean Validation annotations
- **H2 Database** — in-memory database (preloaded with sample data via `data.sql`)
- **SpringDoc / Swagger UI** — auto-generated interactive API documentation
- **Lombok** — boilerplate reduction
- **Maven** — build tool

## Domain Model

The application manages four core entities and their relationships:

```
Client ──< Parcel >── RoutePlan >── Car
```

| Entity | Description |
|---|---|
| `Client` | Customer placing orders; unique email and phone number |
| `Parcel` | Shipment with sender/recipient addresses, weight, price, dispatch and delivery dates |
| `RoutePlan` | Delivery route with start/end location, stops, distance, estimated time, and a scheduled date |
| `Car` | Vehicle assigned to a route; tracked by registration number and cargo capacity |

## Features

- **HATEOAS** — all responses include hypermedia links to related resources (e.g. a parcel response links to its client and route plan)
- **Global exception handling** — centralized `@RestControllerAdvice` returns structured `ErrorResponse` JSON for validation errors, conflicts, type mismatches, and server errors
- **Input validation** — `@NotBlank`, `@Email`, `@Pattern`, `@Positive`, `@DecimalMin` annotations on all DTOs; Polish postal address format enforced via regex
- **Conflict detection** — duplicate email, phone number, or registration number checks with meaningful HTTP 409 responses
- **Sample data** — database is preloaded on startup with 5 clients, 3 cars, 3 routes, and 6 parcels

## Getting Started

### Prerequisites
- Java 21
- Maven 3.8+

### Run

```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080`.

### Swagger UI

```
http://localhost:8080/swagger-ui.html
```

Interactive API documentation with all endpoints, request/response schemas, and the ability to send requests directly from the browser.

### H2 Console

```
http://localhost:8080/h2-console
```

## Address Format

Route plans and stops require addresses in Polish postal format:

```
ul. Przykładowa 10, 00-950 Warszawa
```

Requests with incorrectly formatted addresses are rejected with a `400 Bad Request` and a descriptive error message.
