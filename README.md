# notification-challenge

A REST API microservice for managing users and notifications, with JWT-based authentication.

## Badges

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/bgnoatto/notification-challenge/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/bgnoatto/notification-challenge/tree/main) [![Coverage Status](https://coveralls.io/repos/github/bgnoatto/notification-challenge/badge.svg?branch=main)](https://coveralls.io/github/bgnoatto/notification-challenge?branch=main)

## Features

- Register and authenticate users with JWT
- Create, list, update, and delete notifications
- Create, list, update, and delete users
- Persistent audit log of sent notifications
- Async notification dispatch via Kafka — sending is decoupled from the HTTP request; a consumer processes each notification independently

## Pre-Requisites

- Docker installed
- Docker Compose installed
- Ports free: 8080 and 5432

## How to run the APP

```bash
chmod +x ./run-app.sh
./run-app.sh
```

## How to run the tests

```bash
chmod +x ./run-tests.sh
./run-tests.sh
```

## Areas to improve

- `POST /users` is currently open to anyone. It should require an `X-Api-Key` header so only authorized clients can
  create users.
- Each service (`UserService`, `NotificationService`, `AuthService`) should have a corresponding interface. Controllers
  should depend on the interface, not the concrete class — enforces DIP and makes mocking in tests cleaner.
- `AuthController` handles `BadCredentialsException` with a local `@ExceptionHandler`. That responsibility belongs in a
  dedicated `@ControllerAdvice` so exception-to-HTTP mappings are centralized and consistently testable.

## Techs

- Java 21
- Spring Boot 3.5
- Spring Security + JWT (jwt 0.12)
- Spring Data JPA / Hibernate
- PostgreSQL 15
- Testcontainers
- JUnit 5
- JaCoCo + Coveralls

## Decisions made

- **Spring Boot**: Industry standard for Java microservices. Built-in support for security, JPA, and testing makes it
  the natural choice.
- **JWT**: Stateless authentication — no session storage needed, scales horizontally without shared state.
- **JPA / Hibernate**: Standard ORM for Spring applications. Lets us work at the object level without writing raw SQL
  for core operations.
- **Testcontainers**: Tests run against a real PostgreSQL instance, not an in-memory mock. Eliminates the class of bugs
  that only surface against a real database.
- **Docker / Docker Compose**: Single command to spin up both app and database. Portable across environments.

## Route

- Local: [API Swagger](http://localhost:8080/swagger/api)
- Deployed in: [API Swagger](https://notification-challenge-b3b62f45ed5a.herokuapp.com/swagger/api)

## Env vars

| Variable      | Description                                  | Default                             |
|---------------|----------------------------------------------|-------------------------------------|
| `JWT_SECRET`  | Base64-encoded secret for signing JWT tokens | insecure default — override in prod |
| `DB_HOST`     | PostgreSQL host                              | `localhost`                         |
| `DB_NAME`     | Database name                                | `notification`                      |
| `DB_USER`     | Database user                                | `myuser`                            |
| `DB_PASSWORD` | Database password                            | `secret`                            |

---

## Code Conventions

### Do not use Lombok on JPA entities

`@Data` generates `equals()` and `hashCode()` based on all fields. On JPA entities this is an antipattern: the `id` is
`null` before persisting, which means `hashCode` changes after saving. This breaks collections like `HashSet` or
`HashMap`.

Use explicit getters and setters, formatted in multiple lines.

### Use MAX_VALUE for non-existent IDs in tests

When testing "not found" scenarios, use `Long.MAX_VALUE` or `Integer.MAX_VALUE` instead of arbitrary numbers like `999`
or `-1`. It makes the intent explicit — that value will never exist as a real DB-generated ID.

### Exclude main class from JaCoCo coverage

`NotificationChallengeApplication` is excluded from the JaCoCo report. It contains zero business logic — its only
responsibility is delegating to `SpringApplication.run()`. Testing it would require spinning up the full application
context without adding any meaningful coverage signal.

### Exclude JPA entities from JaCoCo coverage

Pure JPA entities (e.g. `NotificationLog`) are excluded from the JaCoCo report. They contain no business logic —
only getters, setters, and persistence annotations. Coverage on these classes produces noise without signaling
anything about correctness.
