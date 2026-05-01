package bgn.source.notification.repository;

import bgn.source.notification.model.Notification;
import bgn.source.notification.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
  List<Notification> findByUser(User user);
}
