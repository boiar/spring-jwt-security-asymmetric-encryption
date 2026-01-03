# HMVC TODO App 
## JWT Security with Asymmetric Encryption (RSA)

This project demonstrates a robust, production-ready authentication and authorization system built with Spring Boot 3.5.3, following an HMVC (Hierarchical Model–View–Controller) architecture.

The application secures APIs using JSON Web Tokens (JWT) with Asymmetric Encryption (RSA), providing a higher security level compared to traditional symmetric (shared-secret) JWT implementations.

## Key Features

*   **Asymmetric Encryption**: RSA keys (Private/Public) for JWT signing and verification.
*   **Event-Driven Email Notifications:** Asynchronous welcome email delivery using RabbitMQ message broker.
*   **Token Management**: Access Token (short-lived) and Refresh Token (long-lived) implementation.
*   **OpenAPI Documentation**: Integrated Swagger UI for API exploration.
*   **Data Validation**: Comprehensive input validation using Jakarta Validation.
*   **Docker Support**: Full Docker and Docker Compose setup for easy deployment.
*   **Global Exception Handling**: Centralized error handling for consistent API responses.

## Technology Stack

*   **Java 21**
*   **Spring Boot 3.5.3**
*   **Spring Security**
*   **PostgreSQL**
*   **RabbitMQ**
*   **JJWT (Java JWT)**
*   **Lombok**
*   **Maven**
*   **Docker & Docker Compose**

## Prerequisites

Ensure you have the following installed:

*   **Java 21 SDK**
*   **Maven 3.8+**
*   **Docker & Docker Compose**

## Configuration & Setup

### 1. Database Setup
Start the PostgreSQL database using Docker Compose:

```bash
docker-compose up -d
```
*Note: This starts a Postgres container on port 5432.*

### 2. Environment Variables
Create a `.env` file in the root directory. You can copy `.env.example` as a starting point, but you **must** add the JWT key paths.

**Create `.env`:**
```env
# Database Configuration
DB_URL=localhost
DB_PORT=5432
DB_NAME=spring_app_db
DB_USERNAME=username
DB_PASSWORD=password

# JWT Asymmetric Keys (Paths relative to classpath resources)
APP_SECURITY_JWT_PRIVATE_KEY_PATH=/keys/local-only/private_key.pem
APP_SECURITY_JWT_PUBLIC_KEY_PATH=/keys/local-only/public_key.pem

# Other Settings
APP_NAME=auth-security
```
*The project includes demo keys in `src/main/resources/keys/local-only`. For production, **ALWAYS** generate new keys.*

### 3. Running the Application

**Using Maven:**
```bash
./mvnw spring-boot:run
```

**Using Docker:**
```bash
docker-compose -f docker-compose.dev.yml up --build
```

## Generating New RSA Keys
To generate your own keys for production:

1.  **Generate Private Key:**
    ```bash
    openssl genrsa -out private_key.pem 2048
    ```
2.  **Generate Public Key:**
    ```bash
    openssl rsa -in private_key.pem -pubout -out public_key.pem
    ```
3.  **Convert Private Key to PKCS8 (Required for Java):**
    ```bash
    openssl pkcs8 -topk8 -inform PEM -in private_key.pem -out private_key_pkcs8.pem -nocrypt
    ```
4.  Place the files in `src/main/resources/keys/` and update your `.env` path variables accordingly.






## API Documentation
Once the application is running, access the interactive Swagger UI:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Project Structure
```
src/main/java/com/example/auth_security/
├── auth/           # Login/Registration logic
├── common/         # Shared entities, configs, and utilities
├── core/           # Core configs, Security filters, KeyUtils
├── user/           # User management
├── todo/           # Todo management
├── category/       # Category management (for todos)
└── Application.java
```
