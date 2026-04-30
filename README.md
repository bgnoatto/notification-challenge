# notification-challenge

## Code Conventions

### Do not use Lombok on JPA entities

`@Data` generates `equals()` and `hashCode()` based on all fields. On JPA entities this is an anti-pattern: the `id` is
`null` before persisting, which means `hashCode` changes after saving. This breaks collections like `HashSet` or
`HashMap`.

Use explicit getters and setters, formatted in multiple lines.

[![CircleCI](https://dl.circleci.com/status-badge/img/circleci/TWozeTEaidjapipifmCiFG/LiWUVJ7gumau6S5o9DCYF5/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/circleci/TWozeTEaidjapipifmCiFG/LiWUVJ7gumau6S5o9DCYF5/tree/main) [![Coverage Status](https://coveralls.io/repos/github/bgnoatto/notification-challenge/badge.svg)](https://coveralls.io/github/bgnoatto/notification-challenge)