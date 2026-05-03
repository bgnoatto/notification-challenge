package bgn.source.notification.service;

import bgn.source.notification.model.NotificationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {

	private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

	private final String topicName;

	public NotificationProducer(KafkaTemplate<String, NotificationEvent> kafkaTemplate,
			@Value("${notification.topic.name}") String topicName) {
		this.kafkaTemplate = kafkaTemplate;
		this.topicName = topicName;
	}

	public void publish(NotificationEvent event) {
		kafkaTemplate.send(topicName, String.valueOf(event.notificationId()), event);
	}

}
