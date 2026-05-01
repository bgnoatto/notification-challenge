package bgn.source.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(@Schema(example = "juanito99") String userName,
		@Schema(example = "123456") String password) {
}
