package bgn.source.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserRequest(
    @Schema(example = "juan") String name,
    @Schema(example = "juan@mail.com") String email,
    @Schema(example = "123456") String password) {}
