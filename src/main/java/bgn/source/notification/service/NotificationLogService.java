package bgn.source.notification.service;

import bgn.source.notification.model.Notification;
import bgn.source.notification.model.NotificationChannel;
import bgn.source.notification.model.NotificationLog;
import bgn.source.notification.model.NotificationStatus;
import bgn.source.notification.repository.NotificationLogRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class NotificationLogService {

	private final NotificationLogRepository logRepository;

	public NotificationLogService(NotificationLogRepository logRepository) {
		this.logRepository = logRepository;
	}

	public NotificationLog register(Notification notification, NotificationChannel channel, NotificationStatus status,
			String detail) {
		NotificationLog entry = new NotificationLog();
		entry.setNotification(notification);
		entry.setChannel(channel);
		entry.setStatus(status);
		entry.setDetail(detail);
		entry.setSentAt(LocalDateTime.now());
		return logRepository.save(entry);
	}

}
