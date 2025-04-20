# URL Shortener API

A simple Spring Boot application that provides URL shortening functionality. It supports:

- **Shorten URL**: Accepts a long URL and returns a short URL code.
- **Redirect**: Redirects requests from a short URL to the original long URL.
- **Info**: Retrieves metadata (original URL, short URL, creation time) for a given short code.
- **Validation**: Ensures incoming URLs are well‑formed.
- **Error Handling**: Returns appropriate HTTP status codes and JSON error responses for invalid input or resources not found.

---

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Building and Running](#building-and-running)
- [API Endpoints](#api-endpoints)
- [Examples](#examples)
- [Testing](#testing)

---

## Features

1. **Shorten URL** (`POST /api/shorten`)
2. **Redirect** (`GET /{shortCode}`)
3. **Get URL Info** (`GET /api/info/{shortCode}`)
4. **In‑memory H2 database** for storage (switchable to persistent DB)
5. **Validation** of incoming URLs (returns 400 Bad Request if invalid)
6. **Error Handling** with standardized JSON payloads

## Technologies

- Java 17
- Spring Boot 2.7
- Spring Web MVC
- Spring Data JPA
- H2 Database (in‑memory)
- Guava (Murmur3 hashing)
- springdoc-openapi-ui 1.8.0 (OpenAPI/Swagger) (OpenAPI/Swagger)
- JUnit 5, Mockito, Spring Boot Test

## Prerequisites

- Java 17 SDK
- Maven 3.x

## Configuration

All configuration resides in `src/main/resources/application.yml`:

```yaml
app:
  base-url: http://short.ly/

spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driverClassName: org.h2.Driver
    username: sa
    password: 
  jpa:
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.dialect.H2Dialect
  h2:
    console:
      enabled: true
```

- **app.base-url**: The domain prefix for generated short URLs.
- **H2 console** is available at `/h2-console` for database inspection.

## Building and Running

1. **Clone the repository** or **extract the `origin-url-shortner.zip`** archive

   ```bash
   git clone https://your-repo-url.git
   cd url-shortener-api
   ```

2. **Build with Maven**

   ```bash
   mvn clean package
   ```

3. **Run the application**

   ```bash
   java -jar target/origin-url-shortner-1.0-SNAPSHOT.jar
   ```

   The service will start on port **8080** by default.

4. **Import into IDE (e.g., IntelliJ IDEA)**
   - Open the project directory in your IDE.
   - Run `mvn clean install` to build the project.
   - Start the application with:
     ```bash
     mvn spring-boot:run
     ```


## API Endpoints

| Method | Path               | Body / Params                      | Description                                       |
|--------|--------------------|------------------------------------|---------------------------------------------------|
| POST   | `/api/shorten`     | `{ "originalUrl": "<long>" }`  | Returns `{ "shortUrl": "<base-url>/<code>" }`|
| GET    | `/{shortCode}`     | Path variable `shortCode`          | Redirects (302) to the original URL              |
| GET    | `/api/info/{code}` | Path variable `code`               | Returns metadata JSON `{ originalUrl, shortUrl, createdAt }` |

### Error Responses

Errors return a JSON payload:

```json
{
  "title": "url-shortener-error",
  "status": 400,
  "message": "Invalid URL format - ..."
}
```

- **400 Bad Request**: Invalid URL format
- **404 Not Found**: Short code not found

## Examples

1. **Shorten a URL**

   ```bash
   curl -X POST http://localhost:8080/api/shorten \
     -H 'Content-Type: application/json' \
     -d '{"originalUrl":"https://www.originenergy.com.au"}'
   ```

   Response:

   ```json
   {
     "shortUrl": "http://short.ly/a1B2c3d4"
   }
   ```

2. **Redirect**

   ```bash
   curl -i http://localhost:8080/a1B2c3d4
   ```

   Response:

   ```http
   HTTP/1.1 302 Found
   Location: https://www.originenergy.com.au
   ```

3. **Get URL Info**

   ```bash
   curl http://localhost:8080/api/info/a1B2c3d4
   ```

   Response:

   ```json
   {
     "originalUrl": "https://www.originenergy.com.au",
     "shortUrl": "http://short.ly/a1B2c3d4",
     "createdAt": "2025-04-19T20:00:00"
   }
   ```

## OpenAPI Documentation

The API is documented using Springdoc OpenAPI. After starting the application, navigate to:

- OpenAPI JSON: `/v3/api-docs`
- Swagger UI: `/swagger-ui.html`, `/swagger-ui/index.html`, or `/swagger-ui/index.htm`

Add the dependency to your `pom.xml`:



## Testing

- **Unit tests** for service, util, and controller using JUnit 5 & Mockito.
- **Integration tests** with Spring Boot Test and TestRestTemplate.

```bash
mvn test
```

---


