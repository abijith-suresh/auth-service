# Auth Service

Lightweight authentication microservice (Spring Boot, MongoDB, JWT). Handles user registration/login and issues secure JWT tokens.

## Features

* User registration and login
* JWT authentication
* MongoDB integration
* Password hashing
* Easy integration with Spring Boot microservices

## Tech

* Java 21
* Spring Boot 3.4.4
* Spring Security
* Spring Data MongoDB
* JWT (JJWT)
* Gradle (Kotlin DSL)

## Endpoints

| Method | Endpoint         | Description           |
| :----- | :--------------- | :-------------------- |
| POST   | `/auth/register` | Register new user     |
| POST   | `/auth/login`    | Login, get JWT token |

## Quick Start

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/abijith-suresh/auth-service.git
    cd auth-service
    ```

2.  **Start MongoDB (using Docker, for example):**

    ```bash
    docker run -d -p 27017:27017 --name mongo mongo:6
    ```

3.  **Run the application (using Gradle):**

    ```bash
    ./gradlew bootRun
    ```

4.  **Test the endpoints (using curl, as an example):**

    **Register:**

    ```bash
    curl -X POST -H "Content-Type: application/json" -d '{"email": "user@example.com", "password": "password"}' http://localhost:8080/auth/register
    ```

    **Login:**

    ```bash
    curl -X POST -H "Content-Type: application/json" -d '{"email": "user@example.com", "password": "password"}' http://localhost:8080/auth/login
    ```

## License

This project is licensed under the MIT License.
