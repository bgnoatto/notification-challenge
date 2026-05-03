package bgn.source.notification.repository;

import bgn.source.notification.model.Notification;
import bgn.source.notification.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByUser(User user);

	@Query("SELECT n FROM Notification n JOIN FETCH n.user WHERE n.id = :id")
	Optional<Notification> findByIdWithUser(@Param("id") Long id);

}
