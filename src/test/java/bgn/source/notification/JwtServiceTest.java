package bgn.source.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import bgn.source.notification.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

	private static final String SECRET = "bXktc3VwZXItc2VjcmV0LWtleS1mb3ItdGVzdGluZyE=";

	private final JwtService jwtService = new JwtService();

	@BeforeEach
	void setup() {
		ReflectionTestUtils.setField(jwtService, "secret", SECRET);
		ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
	}

	@Test
	void generateToken_extractUsernameReturnsSubject() {
		UserDetails user = mock(UserDetails.class);
		when(user.getUsername()).thenReturn("ana99");

		String token = jwtService.generateToken(user);

		assertThat(jwtService.extractUsername(token)).isEqualTo("ana99");
	}

	@Test
	void isValid_validTokenAndMatchingUser_returnsTrue() {
		UserDetails user = mock(UserDetails.class);
		when(user.getUsername()).thenReturn("ana99");
		String token = jwtService.generateToken(user);

		assertThat(jwtService.isValid(token, user)).isTrue();
	}

	@Test
	void isValid_differentUsername_returnsFalse() {
		UserDetails tokenOwner = mock(UserDetails.class);
		when(tokenOwner.getUsername()).thenReturn("ana99");
		String token = jwtService.generateToken(tokenOwner);

		UserDetails other = mock(UserDetails.class);
		when(other.getUsername()).thenReturn("carlos");

		assertThat(jwtService.isValid(token, other)).isFalse();
	}

}
