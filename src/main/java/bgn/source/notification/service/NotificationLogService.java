package bgn.source.notification.service;

import bgn.source.notification.model.Notification;
import bgn.source.notification.model.NotificationChannel;
import bgn.source.notification.model.NotificationLog;
import bgn.source.notification.model.NotificationStatus;
import bgn.source.notification.repository.NotificationLogRepository;
import bgn.source.notification.repository.NotificationRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class NotificationLogService {

	private final NotificationLogRepository logRepository;

	private final NotificationRepository notificationRepository;

	public NotificationLogService(NotificationLogRepository logRepository,
			NotificationRepository notificationRepository) {
		this.logRepository = logRepository;
		this.notificationRepository = notificationRepository;
	}

	public NotificationLog register(Notification notification, NotificationChannel channel, NotificationStatus status,
			String detail) {
		notificationRepository.updateStatus(notification.getId(), status);
		NotificationLog entry = new NotificationLog();
		entry.setNotification(notification);
		entry.setChannel(channel);
		entry.setStatus(status);
		entry.setDetail(detail);
		entry.setSentAt(Instant.now());
		return logRepository.save(entry);
	}

}
