# notification-challenge

## Code Conventions

### External API clients are excluded from coverage

`PokeApiClient` is excluded from JaCoCo coverage. Testing it would mean testing the behavior of an external API, not our
own logic. The HTTP wiring is delegated entirely to Spring's `RestClient`.

### Do not use Lombok on JPA entities

`@Data` generates `equals()` and `hashCode()` based on all fields. On JPA entities this is an anti-pattern: the `id` is
`null` before persisting, which means `hashCode` changes after saving. This breaks collections like `HashSet` or
`HashMap`.

Use explicit getters and setters, formatted in multiple lines.

## Code Conventions

### Use MAX_VALUE for non-existent IDs in tests

When testing "not found" scenarios, use `Long.MAX_VALUE` or `Integer.MAX_VALUE` instead of arbitrary numbers like `999` or `-1`. It makes the intent explicit — that value will never exist as a real DB-generated ID.

## Pending

### API Key for user registration

`POST /users` is currently open to anyone. It should require an `X-Api-Key` header so only authorized clients can create users.

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/bgnoatto/notification-challenge/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/bgnoatto/notification-challenge/tree/main) [![Coverage Status](https://coveralls.io/repos/github/bgnoatto/notification-challenge/badge.svg)](https://coveralls.io/github/bgnoatto/notification-challenge)