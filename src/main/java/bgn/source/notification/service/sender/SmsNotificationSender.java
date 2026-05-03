package bgn.source.notification.service.sender;

import bgn.source.notification.model.Notification;
import bgn.source.notification.model.NotificationChannel;
import bgn.source.notification.model.NotificationStatus;
import bgn.source.notification.service.NotificationLogService;
import bgn.source.notification.service.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmsNotificationSender implements NotificationSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmsNotificationSender.class);

	private static final int MAX_SMS_LENGTH = 160;

	private final NotificationLogService logService;

	public SmsNotificationSender(NotificationLogService logService) {
		this.logService = logService;
	}

	@Override
	public NotificationChannel supportedChannel() {
		return NotificationChannel.SMS;
	}

	@Override
	public void send(Notification notification) {
		String content = notification.getContent();

		if (content.length() > MAX_SMS_LENGTH) {
			LOGGER.warn("SMS content exceeds {} chars for notification {}, truncating", MAX_SMS_LENGTH,
					notification.getId());
			content = content.substring(0, MAX_SMS_LENGTH);
		}

		String recipient = notification.getUser().getPhone();
		LOGGER.info("Sending SMS to {}, content {}", recipient, notification.getContent());

		logService.register(notification, NotificationChannel.SMS, NotificationStatus.SENT,
				"Sent to " + recipient + " | chars: " + content.length());
	}

}
