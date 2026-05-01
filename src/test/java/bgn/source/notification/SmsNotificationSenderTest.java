package bgn.source.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import bgn.source.notification.model.Notification;
import bgn.source.notification.model.NotificationChannel;
import bgn.source.notification.model.User;
import bgn.source.notification.service.NotificationLogService;
import bgn.source.notification.service.sender.SmsNotificationSender;
import org.junit.jupiter.api.Test;

class SmsNotificationSenderTest {

	private final NotificationLogService logService = mock(NotificationLogService.class);

	private final SmsNotificationSender sender = new SmsNotificationSender(logService);

	@Test
	void supportedChannel_returnsSms() {
		assertThat(sender.supportedChannel()).isEqualTo(NotificationChannel.SMS);
	}

	@Test
	void send_contentWithinLimit_registersSuccess() {
		Notification notification = buildNotification("A".repeat(156) + " " + NotificationChannel.SMS.getLabel());

		sender.send(notification);

		verify(logService).register(eq(notification), eq(NotificationChannel.SMS), eq("SUCCESS"),
				contains("chars: 160"));
	}

	@Test
	void send_contentExceedsLimit_truncatesAndRegistersSuccess() {
		Notification notification = buildNotification("A".repeat(200));

		sender.send(notification);

		verify(logService).register(eq(notification), eq(NotificationChannel.SMS), eq("SUCCESS"),
				contains("chars: 160"));
	}

	private Notification buildNotification(String content) {
		User user = new User();
		user.setPhone("+5491100000000");
		Notification notification = new Notification();
		notification.setTitle("Title");
		notification.setContent(content);
		notification.setChannel(NotificationChannel.SMS);
		notification.setUser(user);
		return notification;
	}

}
