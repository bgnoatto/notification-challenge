package bgn.source.notification.service;

import bgn.source.notification.dto.CreateNotificationRequest;
import bgn.source.notification.dto.NotificationResponse;
import bgn.source.notification.dto.UpdateNotificationRequest;
import bgn.source.notification.model.Notification;
import bgn.source.notification.model.NotificationChannel;
import bgn.source.notification.model.NotificationEvent;
import bgn.source.notification.model.NotificationLog;
import bgn.source.notification.model.NotificationStatus;
import bgn.source.notification.model.User;
import bgn.source.notification.repository.NotificationRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NotificationService {

	private final NotificationRepository notificationRepository;

	private final NotificationLogService logService;

	private final NotificationProducer producer;

	public NotificationService(NotificationRepository notificationRepository, NotificationLogService logService,
			NotificationProducer producer) {
		this.notificationRepository = notificationRepository;
		this.logService = logService;
		this.producer = producer;
	}

	public List<NotificationResponse> getOwn(User user) {
		return notificationRepository.findByUserOrderedNewestFirst(user)
			.stream()
			.map(NotificationResponse::from)
			.toList();
	}

	public NotificationResponse create(CreateNotificationRequest request, User user) {
		NotificationChannel channel = resolveChannel(request.channelCode());
		Notification notification = new Notification();
		notification.setTitle(request.title());
		notification.setContent(request.content());
		notification.setChannel(channel);
		notification.setUser(user);
		Notification saved = notificationRepository.save(notification);
		NotificationLog log = logService.register(saved, channel, NotificationStatus.SENDING, null);
		producer.publish(new NotificationEvent(saved.getId(), log.getId(), channel.getCode()));
		return NotificationResponse.from(saved, NotificationStatus.SENDING);
	}

	public NotificationResponse update(Long id, UpdateNotificationRequest request, User user) {
		NotificationChannel channel = resolveChannel(request.channelCode());
		Notification notification = findOwnOrThrow(id, user);
		notification.setTitle(request.title());
		notification.setContent(request.content());
		notification.setChannel(channel);
		Notification saved = notificationRepository.save(notification);
		NotificationLog log = logService.register(saved, channel, NotificationStatus.SENDING, null);
		producer.publish(new NotificationEvent(saved.getId(), log.getId(), channel.getCode()));
		return NotificationResponse.from(saved, NotificationStatus.SENDING);
	}

	public void delete(Long id, User user) {
		findOwnOrThrow(id, user);
		notificationRepository.deleteById(id);
	}

	private NotificationChannel resolveChannel(int code) {
		try {
			return NotificationChannel.fromCode(code);
		}
		catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	private Notification findOwnOrThrow(Long id, User user) {
		Notification notification = notificationRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
		if (!notification.getUser().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your notification");
		}
		return notification;
	}

}
