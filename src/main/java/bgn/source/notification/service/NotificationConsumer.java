package bgn.source.notification.service;

import bgn.source.notification.model.Notification;
import bgn.source.notification.model.NotificationChannel;
import bgn.source.notification.model.NotificationEvent;
import bgn.source.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationConsumer.class);

	private final NotificationRepository notificationRepository;

	private final NotificationSenderRegistry senderRegistry;

	public NotificationConsumer(NotificationRepository notificationRepository,
			NotificationSenderRegistry senderRegistry) {
		this.notificationRepository = notificationRepository;
		this.senderRegistry = senderRegistry;
	}

	@KafkaListener(topics = "${notification.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
	public void consume(NotificationEvent event) {
		LOGGER.info("Received notification event: notificationId={}, channel={}", event.notificationId(),
				event.channel());
		Notification notification = notificationRepository.findByIdWithUser(event.notificationId()).orElse(null);
		if (notification == null) {
			LOGGER.warn("Notification {} not found, skipping", event.notificationId());
			return;
		}
		NotificationChannel channel = NotificationChannel.fromCode(event.channel());
		senderRegistry.forChannel(channel).send(notification);
	}

}
