package bgn.source.notification.repository;

import bgn.source.notification.model.Notification;
import bgn.source.notification.model.NotificationStatus;
import bgn.source.notification.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	@Query("SELECT n FROM Notification n WHERE n.user = :user ORDER BY n.createdAt DESC NULLS LAST, n.updatedAt DESC NULLS LAST, n.id DESC")
	List<Notification> findByUserOrderedNewestFirst(@Param("user") User user);

	@Query("SELECT n FROM Notification n JOIN FETCH n.user WHERE n.id = :id")
	Optional<Notification> findByIdWithUser(@Param("id") Long id);

	@Modifying
	@Transactional
	@Query("UPDATE Notification n SET n.status = :status WHERE n.id = :id")
	void updateStatus(@Param("id") Long id, @Param("status") NotificationStatus status);

}
