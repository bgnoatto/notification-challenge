package bgn.source.notification;

import bgn.source.notification.repository.NotificationRepository;
import bgn.source.notification.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;

abstract class BaseIntegrationTest {

  @ServiceConnection
  @SuppressWarnings("unused")
  static final PostgreSQLContainer<?> postgres;

  static {
    postgres = new PostgreSQLContainer<>("postgres:15");
    postgres.start();
  }

  @Autowired private NotificationRepository notificationRepository;
  @Autowired private UserRepository userRepository;

  @BeforeEach
  void cleanDatabase() {
    notificationRepository.deleteAll();
    userRepository.deleteAll();
  }
}
