package bgn.source.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import bgn.source.notification.model.Notification;
import bgn.source.notification.model.NotificationChannel;
import bgn.source.notification.model.NotificationStatus;
import bgn.source.notification.model.User;
import bgn.source.notification.service.NotificationLogService;
import bgn.source.notification.service.sender.PushNotificationSender;
import org.junit.jupiter.api.Test;

class PushNotificationSenderTest {

	private final NotificationLogService logService = mock(NotificationLogService.class);

	private final PushNotificationSender sender = new PushNotificationSender(logService);

	@Test
	void supportedChannel_returnsPush() {
		assertThat(sender.supportedChannel()).isEqualTo(NotificationChannel.PUSH);
	}

	@Test
	void send_validToken_registersSuccess() {
		Notification notification = buildNotification("valid-device-token " + NotificationChannel.PUSH.getLabel());

		sender.send(notification);

		verify(logService).register(eq(notification), eq(NotificationChannel.PUSH), eq(NotificationStatus.SENT),
				anyString());
	}

	@Test
	void send_nullToken_registersFailed() {
		Notification notification = buildNotification(null);

		sender.send(notification);

		verify(logService).register(eq(notification), eq(NotificationChannel.PUSH), eq(NotificationStatus.FAILED),
				anyString());
	}

	@Test
	void send_blankToken_registersFailed() {
		Notification notification = buildNotification("   ");

		sender.send(notification);

		verify(logService).register(eq(notification), eq(NotificationChannel.PUSH), eq(NotificationStatus.FAILED),
				anyString());
	}

	private Notification buildNotification(String deviceToken) {
		User user = new User();
		user.setDeviceToken(deviceToken);
		Notification notification = new Notification();
		notification.setTitle("Title");
		notification.setContent("Content");
		notification.setChannel(NotificationChannel.PUSH);
		notification.setUser(user);
		return notification;
	}

}
