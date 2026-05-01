package bgn.source.notification.service;

import bgn.source.notification.model.Notification;
import bgn.source.notification.model.NotificationChannel;

public interface NotificationSender {

	NotificationChannel supportedChannel();

	void send(Notification notification);

}
