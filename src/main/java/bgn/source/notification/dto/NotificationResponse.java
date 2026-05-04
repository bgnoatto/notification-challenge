package bgn.source.notification.dto;

import bgn.source.notification.model.Notification;

import java.time.LocalDateTime;

public record NotificationResponse(Long id, String title, String content, int channelCode, String channelLabel,
		Long userId, String userName, LocalDateTime createdAt, LocalDateTime updatedAt) {

	public static NotificationResponse from(Notification n) {
		return new NotificationResponse(n.getId(), n.getTitle(), n.getContent(), n.getChannel().getCode(),
				n.getChannel().getLabel(), n.getUser().getId(), n.getUser().getUserName(), n.getCreatedAt(),
				n.getUpdatedAt());
	}
}
