package bgn.source.notification.repository;

import bgn.source.notification.model.Notification;
import bgn.source.notification.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByUser(User user);

}
