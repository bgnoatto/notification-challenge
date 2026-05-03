package bgn.source.notification;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bgn.source.notification.model.Notification;
import bgn.source.notification.model.NotificationChannel;
import bgn.source.notification.model.NotificationEvent;
import bgn.source.notification.repository.NotificationRepository;
import bgn.source.notification.service.NotificationConsumer;
import bgn.source.notification.service.NotificationSender;
import bgn.source.notification.service.NotificationSenderRegistry;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class NotificationConsumerTest {

	private final NotificationRepository notificationRepository = mock(NotificationRepository.class);

	private final NotificationSenderRegistry senderRegistry = mock(NotificationSenderRegistry.class);

	private final NotificationConsumer consumer = new NotificationConsumer(notificationRepository, senderRegistry);

	@Test
	void consume_existingNotification_callsSender() {
		Notification notification = new Notification();
		notification.setChannel(NotificationChannel.EMAIL);
		NotificationSender sender = mock(NotificationSender.class);
		NotificationEvent event = new NotificationEvent(1L, 10L, NotificationChannel.EMAIL.getCode());

		when(notificationRepository.findByIdWithUser(1L)).thenReturn(Optional.of(notification));
		when(senderRegistry.forChannel(NotificationChannel.EMAIL)).thenReturn(sender);

		consumer.consume(event);

		verify(sender).send(notification);
	}

	@Test
	void consume_unknownNotificationId_skips() {
		NotificationEvent event = new NotificationEvent(Long.MAX_VALUE, Long.MAX_VALUE,
				NotificationChannel.EMAIL.getCode());

		when(notificationRepository.findByIdWithUser(Long.MAX_VALUE)).thenReturn(Optional.empty());

		consumer.consume(event);

		verify(senderRegistry, never()).forChannel(NotificationChannel.EMAIL);
	}

}
