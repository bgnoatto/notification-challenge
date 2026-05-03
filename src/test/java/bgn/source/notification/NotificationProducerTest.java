package bgn.source.notification;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import bgn.source.notification.model.NotificationEvent;
import bgn.source.notification.service.NotificationProducer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

class NotificationProducerTest {

	@SuppressWarnings("unchecked")
	private final KafkaTemplate<String, NotificationEvent> kafkaTemplate = mock(KafkaTemplate.class);

	private final NotificationProducer producer = new NotificationProducer(kafkaTemplate, "notifications");

	@Test
	void publish_sendsEventToTopicWithNotificationIdAsKey() {
		NotificationEvent event = new NotificationEvent(42L, 7L, 1);

		producer.publish(event);

		verify(kafkaTemplate).send("notifications", "42", event);
	}

}
