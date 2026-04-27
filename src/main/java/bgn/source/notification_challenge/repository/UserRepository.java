package bgn.source.notification_challenge.repository;

import bgn.source.notification_challenge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
