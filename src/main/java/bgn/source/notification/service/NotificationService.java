package bgn.source.notification.service;

import bgn.source.notification.dto.CreateNotificationRequest;
import bgn.source.notification.dto.NotificationResponse;
import bgn.source.notification.dto.UpdateNotificationRequest;
import bgn.source.notification.model.Notification;
import bgn.source.notification.model.NotificationChannel;
import bgn.source.notification.model.User;
import bgn.source.notification.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NotificationService {

	private final NotificationRepository notificationRepository;

	private final NotificationSenderRegistry senderRegistry;

	public NotificationService(NotificationRepository notificationRepository,
			NotificationSenderRegistry senderRegistry) {
		this.notificationRepository = notificationRepository;
		this.senderRegistry = senderRegistry;
	}

	public List<NotificationResponse> getOwn(User user) {
		return notificationRepository.findByUser(user).stream().map(NotificationResponse::from).toList();
	}

	public NotificationResponse create(CreateNotificationRequest request, User user) {
		NotificationChannel channel = resolveChannel(request.channelCode());
		Notification notification = new Notification();
		notification.setTitle(request.title());
		notification.setContent(request.content());
		notification.setChannel(channel);
		notification.setUser(user);
		Notification saved = notificationRepository.save(notification);
		senderRegistry.forChannel(channel).send(saved);
		return NotificationResponse.from(saved);
	}

	public NotificationResponse update(Long id, UpdateNotificationRequest request, User user) {
		NotificationChannel channel = resolveChannel(request.channelCode());
		Notification notification = findOwnOrThrow(id, user);
		notification.setTitle(request.title());
		notification.setContent(request.content());
		notification.setChannel(channel);
		return NotificationResponse.from(notificationRepository.save(notification));
	}

	public void delete(Long id, User user) {
		findOwnOrThrow(id, user);
		notificationRepository.deleteById(id);
	}

	private NotificationChannel resolveChannel(int code) {
		try {
			return NotificationChannel.fromCode(code);
		} catch (IllegalArgumentException e) {
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
