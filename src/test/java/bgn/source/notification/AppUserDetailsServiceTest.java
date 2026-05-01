package bgn.source.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import bgn.source.notification.model.User;
import bgn.source.notification.repository.UserRepository;
import bgn.source.notification.security.AppUserDetailsService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class AppUserDetailsServiceTest {

	private final UserRepository userRepository = mock(UserRepository.class);

	private final AppUserDetailsService service = new AppUserDetailsService(userRepository);

	@Test
	void loadUserByUsername_userExists_returnsUser() {
		User user = new User();
		user.setUserName("ana99");
		when(userRepository.findByUserName("ana99")).thenReturn(Optional.of(user));

		assertThat(service.loadUserByUsername("ana99")).isEqualTo(user);
	}

	@Test
	void loadUserByUsername_userNotFound_throwsException() {
		when(userRepository.findByUserName("ghost")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.loadUserByUsername("ghost")).isInstanceOf(UsernameNotFoundException.class)
			.hasMessageContaining("ghost");
	}

}
