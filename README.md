# notification-challenge

## Code Conventions

### Do not use Lombok on JPA entities

`@Data` generates `equals()` and `hashCode()` based on all fields. On JPA entities this is an anti-pattern: the `id` is
`null` before persisting, which means `hashCode` changes after saving. This breaks collections like `HashSet` or
`HashMap`.

Use explicit getters and setters, formatted in multiple lines.

## Code Conventions

### Use MAX_VALUE for non-existent IDs in tests

When testing "not found" scenarios, use `Long.MAX_VALUE` or `Integer.MAX_VALUE` instead of arbitrary numbers like `999`
or `-1`. It makes the intent explicit — that value will never exist as a real DB-generated ID.

## Code Conventions

### Exclude main class from JaCoCo coverage

`NotificationChallengeApplication` is excluded from the JaCoCo report. It contains zero business logic — its only
responsibility is delegating to `SpringApplication.run()`. Testing it would require spinning up the full application
context without adding any meaningful coverage signal.

### Exclude JPA entities from JaCoCo coverage

Pure JPA entities (e.g. `NotificationLog`) are excluded from the JaCoCo report. They contain no business logic —
only getters, setters, and persistence annotations. Coverage on these classes produces noise without signaling
anything about correctness.

## Pending

### API Key for user registration

`POST /users` is currently open to anyone. It should require an `X-Api-Key` header so only authorized clients can create
users.

### Extract service interfaces

Each service (`UserService`, `NotificationService`, `AuthService`, etc.) should have a corresponding interface (e.g.
`IUserService`). The current classes become the implementations. Controllers should depend on the interface, not the
concrete class — this enforces DIP, makes mocking in tests cleaner, and decouples the web layer from the
implementation.

### Move exception handling to a global ControllerAdvice

`AuthController` currently handles `BadCredentialsException` with a local `@ExceptionHandler`. That responsibility
belongs in a dedicated `@ControllerAdvice` class so exception-to-HTTP mappings are centralized, consistent across
controllers, and independently testable.

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/bgnoatto/notification-challenge/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/bgnoatto/notification-challenge/tree/main) [![Coverage Status](https://coveralls.io/repos/github/bgnoatto/notification-challenge/badge.svg)](https://coveralls.io/github/bgnoatto/notification-challenge)