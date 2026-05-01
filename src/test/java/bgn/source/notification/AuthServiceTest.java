package bgn.source.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import bgn.source.notification.dto.LoginRequest;
import bgn.source.notification.model.User;
import bgn.source.notification.repository.UserRepository;
import bgn.source.notification.security.JwtService;
import bgn.source.notification.service.AuthService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.server.ResponseStatusException;

class AuthServiceTest {

	private final UserRepository userRepository = mock(UserRepository.class);

	private final JwtService jwtService = mock(JwtService.class);

	private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);

	private final AuthService authService = new AuthService(userRepository, jwtService, authenticationManager);

	@Test
	void login_validCredentials_returnsToken() {
		User user = new User();
		user.setUserName("ana99");
		when(authenticationManager.authenticate(any())).thenReturn(null);
		when(userRepository.findByUserName("ana99")).thenReturn(Optional.of(user));
		when(jwtService.generateToken(user)).thenReturn("test-token");

		assertThat(authService.login(new LoginRequest("ana99", "pass")).token()).isEqualTo("test-token");
	}

	@Test
	void login_invalidCredentials_throwsException() {
		when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad credentials"));

		assertThatThrownBy(() -> authService.login(new LoginRequest("ana99", "wrong")))
			.isInstanceOf(BadCredentialsException.class);
	}

	@Test
	void login_userNotFoundAfterAuth_throws401() {
		when(authenticationManager.authenticate(any())).thenReturn(null);
		when(userRepository.findByUserName("ghost")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> authService.login(new LoginRequest("ghost", "pass")))
			.isInstanceOf(ResponseStatusException.class);
	}

}
