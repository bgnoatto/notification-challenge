package bgn.source.notification.service.sender;

import bgn.source.notification.model.Notification;
import bgn.source.notification.model.NotificationChannel;
import bgn.source.notification.service.NotificationLogService;
import bgn.source.notification.service.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PushNotificationSender implements NotificationSender {

	private static final Logger log = LoggerFactory.getLogger(PushNotificationSender.class);

	private final NotificationLogService logService;

	public PushNotificationSender(NotificationLogService logService) {
		this.logService = logService;
	}

	@Override
	public NotificationChannel supportedChannel() {
		return NotificationChannel.PUSH;
	}

	@Override
	public void send(Notification notification) {
		String token = notification.getUser().getDeviceToken();

		if (token == null || token.isBlank()) {
			log.warn("Missing device token for user {}", notification.getUser().getId());
			logService.register(notification, NotificationChannel.PUSH, "FAILED", "Missing device token");
			return;
		}

		String payload = buildPayload(notification);
		log.info("Sending push to token {} | payload: {}", token, payload);
		logService.register(notification, NotificationChannel.PUSH, "SUCCESS",
				"Token: " + token + " | payload: " + payload);
	}

	private String buildPayload(Notification notification) {
		return "{\"title\":\"" + notification.getTitle() + "\",\"body\":\"" + notification.getContent() + "\"}";
	}

}
