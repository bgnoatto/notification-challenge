package bgn.source.notification;

import bgn.source.notification.model.Notification;
import bgn.source.notification.model.NotificationChannel;
import bgn.source.notification.model.NotificationEvent;
import bgn.source.notification.model.NotificationLog;
import bgn.source.notification.model.NotificationStatus;
import bgn.source.notification.model.User;
import bgn.source.notification.repository.NotificationLogRepository;
import bgn.source.notification.repository.NotificationRepository;
import bgn.source.notification.repository.UserRepository;
import bgn.source.notification.service.NotificationLogService;
import bgn.source.notification.service.NotificationProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "notifications", bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@TestPropertySource(properties = "spring.kafka.listener.auto-startup=true")
@DirtiesContext
class NotificationKafkaFlowTest {

	@ServiceConnection
	@SuppressWarnings("unused")
	static final PostgreSQLContainer<?> postgres;

	static {
		postgres = new PostgreSQLContainer<>("postgres:15");
		postgres.start();
	}

	@Autowired
	private NotificationProducer producer;

	@Autowired
	private NotificationLogService logService;

	@Autowired
	private NotificationLogRepository logRepository;

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void clean() {
		logRepository.deleteAll();
		notificationRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void publishEvent_emailNotification_consumerLogsSent() {
		User user = buildUser("kafka@test.com");
		userRepository.save(user);
		Notification notification = buildNotification(user, NotificationChannel.EMAIL);
		notificationRepository.save(notification);
		NotificationLog log = logService.register(notification, NotificationChannel.EMAIL, NotificationStatus.SENDING,
				null);

		producer.publish(new NotificationEvent(notification.getId(), log.getId(), NotificationChannel.EMAIL.getCode()));

		await().atMost(10, TimeUnit.SECONDS)
			.untilAsserted(
					() -> assertThat(logRepository.findAll()).anyMatch(l -> l.getStatus() == NotificationStatus.SENT));
	}

	private User buildUser(String email) {
		User user = new User();
		user.setName("Test");
		user.setLastName("User");
		user.setUserName(email);
		user.setEmail(email);
		user.setPassword("hashed");
		return user;
	}

	private Notification buildNotification(User user, NotificationChannel channel) {
		Notification notification = new Notification();
		notification.setTitle("Test " + channel.getLabel());
		notification.setContent("Content " + channel.getLabel());
		notification.setChannel(channel);
		notification.setUser(user);
		return notification;
	}

}
