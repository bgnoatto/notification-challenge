package bgn.source.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateUserRequest(@Schema(example = "juan") String name, @Schema(example = "perez") String lastName,
		@Schema(example = "juanito99") String userName, @Schema(example = "juan@mail.com") String email,
		@Schema(example = "+5491112345678") String phone,
		@Schema(example = "550e8400-e29b-41d4-a716-446655440000") String deviceToken,
		@Schema(example = "123456") String password) {
}
