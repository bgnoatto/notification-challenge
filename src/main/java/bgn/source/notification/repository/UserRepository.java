package bgn.source.notification.repository;

import bgn.source.notification.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  Optional<User> findByUserName(String userName);

  boolean existsByEmail(String email);

  boolean existsByEmailAndIdNot(String email, Long id);
}
