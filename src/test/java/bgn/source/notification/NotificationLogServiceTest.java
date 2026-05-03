package bgn.source.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import bgn.source.notification.model.Notification;
import bgn.source.notification.model.NotificationChannel;
import bgn.source.notification.model.NotificationLog;
import bgn.source.notification.model.NotificationStatus;
import bgn.source.notification.repository.NotificationLogRepository;
import bgn.source.notification.service.NotificationLogService;
import org.junit.jupiter.api.Test;

class NotificationLogServiceTest {

	private final NotificationLogRepository logRepository = mock(NotificationLogRepository.class);

	private final NotificationLogService logService = new NotificationLogService(logRepository);

	@Test
	void register_returnsPersistedLog() {
		Notification notification = new Notification();
		NotificationLog saved = new NotificationLog();
		when(logRepository.save(any())).thenReturn(saved);

		NotificationLog result = logService.register(notification, NotificationChannel.EMAIL,
				NotificationStatus.SENDING, null);

		assertThat(result).isSameAs(saved);
	}

}
