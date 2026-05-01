package bgn.source.notification;

import bgn.source.notification.model.Notification;
import bgn.source.notification.model.NotificationChannel;
import bgn.source.notification.model.User;
import bgn.source.notification.service.NotificationLogService;
import bgn.source.notification.service.sender.EmailNotificationSender;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EmailNotificationSenderTest {

	private final NotificationLogService logService = mock(NotificationLogService.class);

	private final EmailNotificationSender sender = new EmailNotificationSender(logService);

	@Test
	void supportedChannel_returnsEmail() {
		assertThat(sender.supportedChannel()).isEqualTo(NotificationChannel.EMAIL);
	}

	@Test
	void send_validEmail_registersSuccess() {
		Notification notification = buildNotification("Alert " + NotificationChannel.EMAIL.getLabel(),
				"Content " + NotificationChannel.EMAIL.getLabel(), "valid@test.com");

		sender.send(notification);

		verify(logService).register(eq(notification), eq(NotificationChannel.EMAIL), eq("SUCCESS"), anyString());
	}

	@Test
	void send_invalidEmail_registersFailed() {
		Notification notification = buildNotification("Alert " + NotificationChannel.EMAIL.getLabel(),
				"Content " + NotificationChannel.EMAIL.getLabel(), "not-an-email");

		sender.send(notification);

		verify(logService).register(eq(notification), eq(NotificationChannel.EMAIL), eq("FAILED"), anyString());
	}

	private Notification buildNotification(String title, String content, String email) {
		User user = new User();
		user.setEmail(email);
		Notification notification = new Notification();
		notification.setTitle(title);
		notification.setContent(content);
		notification.setChannel(NotificationChannel.EMAIL);
		notification.setUser(user);
		return notification;
	}

}
