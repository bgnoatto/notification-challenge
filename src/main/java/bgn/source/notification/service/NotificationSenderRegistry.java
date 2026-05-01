package bgn.source.notification.service;

import bgn.source.notification.model.NotificationChannel;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class NotificationSenderRegistry {

	private final Map<NotificationChannel, NotificationSender> senders;

	public NotificationSenderRegistry(List<NotificationSender> senders) {
		this.senders = senders.stream()
			.collect(Collectors.toMap(NotificationSender::supportedChannel, Function.identity()));
	}

	public NotificationSender forChannel(NotificationChannel channel) {
		NotificationSender sender = senders.get(channel);
		if (sender == null) {
			throw new IllegalStateException("No sender registered for channel: " + channel);
		}
		return sender;
	}

}
