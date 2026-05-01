package bgn.source.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateNotificationRequest(
    @Schema(example = "Alerta de sistema") String title,
    @Schema(example = "El servidor está al 90% de capacidad.") String content,
    @Schema(example = "1") int channelCode) {}
