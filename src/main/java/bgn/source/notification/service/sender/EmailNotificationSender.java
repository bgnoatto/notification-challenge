package bgn.source.notification.service.sender;

import bgn.source.notification.model.Notification;
import bgn.source.notification.model.NotificationChannel;
import bgn.source.notification.service.NotificationLogService;
import bgn.source.notification.service.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationSender implements NotificationSender {

	private static final Logger log = LoggerFactory.getLogger(EmailNotificationSender.class);

	private static final String EMAIL_REGEX = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

	private final NotificationLogService logService;

	public EmailNotificationSender(NotificationLogService logService) {
		this.logService = logService;
	}

	@Override
	public NotificationChannel supportedChannel() {
		return NotificationChannel.EMAIL;
	}

	@Override
	public void send(Notification notification) {
		String recipient = notification.getUser().getEmail();

		if (!recipient.matches(EMAIL_REGEX)) {
			log.warn("Invalid email for user {}: {}", notification.getUser().getId(), recipient);
			logService.register(notification, NotificationChannel.EMAIL, "FAILED", "Invalid email: " + recipient);
			return;
		}

		String template = buildTemplate(notification);
		log.info("Sending email to {} | template: {}", recipient, template);
		logService.register(notification, NotificationChannel.EMAIL, "SUCCESS",
				"Sent to " + recipient + " | template: " + template);
	}

	private String buildTemplate(Notification notification) {
		return "Subject: " + notification.getTitle() + " | Body: " + notification.getContent();
	}

}
