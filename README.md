# User Search Backend

This is the backend service for the User Search application. It provides RESTful APIs to retrieve user data, supports free-text search using Hibernate Search, and loads user data from an external API (`https://dummyjson.com/users`) into an in-memory H2 database.

## Prerequisites
- Java 17+ → [Download](https://adoptium.net/)
- Maven → [Download](https://maven.apache.org/download.cgi)
- (Optional) Postman for API testing → [Download](https://www.postman.com/)

## Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/aanchal-0114/user-search-api.git
   cd user-search-backend
   ```

2. Build the project:
   ```sh
   mvn clean install
   ```

3. Run the application:
   ```sh
   mvn spring-boot:run
   ```
   The backend will start on **http://localhost:8080**.

## Features
- Loads user data from `https://dummyjson.com/users` into H2 DB  
- Search users by first name, last name, or SSN (Hibernate Search)  
- Retrieve users by ID and email  
- Exception handling and validation  
- OpenAPI/Swagger documentation  
- Logs and structured error messages  
- Environment-based configuration  
- Unit and integration tests  

## API Endpoints

### User Search & Retrieval
- **GET** `/api/users/search?query={text}` → Search users by first name, last name, or SSN  
- **GET** `/api/users/{id}` → Retrieve a user by ID  
- **GET** `/api/users/email/{email}` → Retrieve a user by email  

### API Documentation
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)  

## Configuration

Modify `application.yml` for environment-specific settings.

Example:
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:userdb
    driver-class-name: org.h2.Driver
    username: sa
    password:

  h2:
    console:
      enabled: true
      path: /h2-console

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

external:
  user-api: "https://dummyjson.com/users"
```

## Project Structure

```
user-search-backend/
 ├── src/main/java/com/example/usersearch/
 │   ├── config/         # Configuration classes
 │   ├── controller/     # REST controllers
 │   ├── service/        # Business logic
 │   ├── repository/     # Data access layer
 │   ├── model/          # Entity and DTO classes
 │   ├── exception/      # Custom exceptions and handlers
 │   ├── utils/          # Utility classes
 │   ├── UserSearchApplication.java  # Main entry point
 ├── src/main/resources/
 │   ├── application.yml # Configuration file
 │   ├── schema.sql      # (Optional) DB schema
 ├── pom.xml             # Maven dependencies
 ├── README.md           # Documentation
```

## Running Tests
To run unit and integration tests:
```sh
mvn test
```

## Deployment

### Build JAR
```sh
mvn clean package
```
The JAR file will be generated in `target/`. Run it using:
```sh
java -jar target/user-search-backend-0.0.1-SNAPSHOT.jar
```

## Contributing
Pull requests are welcome. Please open an issue first for any major changes.

## License
MIT License

