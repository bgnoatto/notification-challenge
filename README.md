# notification-challenge

## Code Conventions

### Do not use Lombok on JPA entities

`@Data` generates `equals()` and `hashCode()` based on all fields. On JPA entities this is an anti-pattern: the `id` is `null` before persisting, which means `hashCode` changes after saving. This breaks collections like `HashSet` or `HashMap`.

Use explicit getters and setters, formatted in multiple lines.