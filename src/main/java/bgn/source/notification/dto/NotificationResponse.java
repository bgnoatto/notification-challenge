package bgn.source.notification.dto;

import bgn.source.notification.model.Notification;
import bgn.source.notification.model.NotificationStatus;
import java.time.Instant;

public record NotificationResponse(Long id, String title, String content, int channelCode, String channelLabel,
		Long userId, String userName, Instant createdAt, Instant updatedAt, Integer statusCode, String statusLabel) {

	public static NotificationResponse from(Notification n) {
		return from(n, n.getStatus());
	}

	public static NotificationResponse from(Notification n, NotificationStatus status) {
		return new NotificationResponse(n.getId(), n.getTitle(), n.getContent(), n.getChannel().getCode(),
				n.getChannel().getLabel(), n.getUser().getId(), n.getUser().getUserName(), n.getCreatedAt(),
				n.getUpdatedAt(), status != null ? status.getCode() : null, status != null ? status.getLabel() : null);
	}
}
