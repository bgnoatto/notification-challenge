package bgn.source.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateNotificationRequest(@Schema(example = "Alerta actualizada") String title,
		@Schema(example = "Contenido actualizado.") String content, @Schema(example = "2") int channelCode) {
}
