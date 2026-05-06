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
- Updating a notification (`PUT /notifications/{id}`) re-dispatches it through the indicated channel without creating a new record
- Each notification exposes its current status (`SENDING`, `SENT`, `FAILED`) — denormalized directly on the record for efficient reads
- All timestamps stored as UTC `Instant` — timezone conversion is a presentation-layer concern

## Pre-Requisites

- Docker installed
- Docker Compose installed
- Ports free: 8080, 5432 and 9092

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
- **UTC timestamps (`Instant`)**: All dates are stored as `Instant` (UTC). `LocalDateTime` was discarded because it carries no timezone and behaves differently depending on the JVM's default zone. Conversion to local time is a presentation concern.
- **Denormalized `status` on `Notification`**: The current notification status is stored directly on the `Notification` table instead of being derived from the latest `NotificationLog`. Avoids a `MAX + GROUP BY` subquery on every list read. Updated via a targeted JPQL `@Modifying` query that bypasses the entity lifecycle — preventing accidental `updatedAt` writes.
- **`updatedAt` via `@PreUpdate`**: Replaced `@UpdateTimestamp` (fires on every `save()`, including INSERT) with a `@PreUpdate` JPA callback. `updatedAt` is now `null` on creation and only populated on real business updates.
- **`createdAt` set by the database**: Removed `@CreationTimestamp`. The column uses `DEFAULT now()` at the DB level; Hibernate marks it `insertable = false` and re-fetches the value after INSERT via `@Generated(event = EventType.INSERT)`.
- **Kafka startup ordering**: The `app` service declares `depends_on: kafka: condition: service_healthy`. Without this, the app starts before the broker is ready and spams connection errors. Inside Docker the broker is reachable at `kafka:29092` (internal listener), not `localhost:9092` (host listener).

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
| `DB_PASSWORD`             | Database password                                      | `secret`         |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker address (use `kafka:29092` inside Docker) | `localhost:9092` |

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

### Always use UTC (`Instant`) for timestamps

Never use `LocalDateTime` for persisted dates. It has no timezone information and silently takes the JVM's default zone,
which differs between local dev, CI, and production containers. Use `java.time.Instant` — it always represents a
precise UTC point in time. Convert to local time only at the presentation layer.

### Status updates bypass the JPA entity lifecycle

When updating only the `status` field on `Notification`, use the `@Modifying` JPQL query
(`NotificationRepository.updateStatus`) instead of loading the entity and calling `save()`. Loading + saving triggers
`@PreUpdate`, which sets `updatedAt` — incorrect for a status-only transition. The direct query skips the lifecycle
entirely.
