package bgn.source.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bgn.source.notification.security.JwtAuthFilter;
import bgn.source.notification.security.JwtService;
import jakarta.servlet.FilterChain;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

class JwtAuthFilterTest {

	private final JwtService jwtService = mock(JwtService.class);

	private final UserDetailsService userDetailsService = mock(UserDetailsService.class);

	private final JwtAuthFilter filter = new JwtAuthFilter(jwtService, userDetailsService);

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void doFilter_noAuthHeader_chainContinuesWithoutAuth() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);

		filter.doFilter(request, response, chain);

		verify(chain).doFilter(request, response);
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
	}

	@Test
	void doFilter_nonBearerHeader_chainContinuesWithoutAuth() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Basic dXNlcjpwYXNz");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);

		filter.doFilter(request, response, chain);

		verify(chain).doFilter(request, response);
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
	}

	@Test
	void doFilter_validToken_setsAuthentication() throws Exception {
		UserDetails user = mock(UserDetails.class);
		when(user.getUsername()).thenReturn("ana99");
		when(user.getAuthorities()).thenReturn(List.of());
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer valid-token");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);

		when(jwtService.extractUsername("valid-token")).thenReturn("ana99");
		when(userDetailsService.loadUserByUsername("ana99")).thenReturn(user);
		when(jwtService.isValid("valid-token", user)).thenReturn(true);

		filter.doFilter(request, response, chain);

		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
		verify(chain).doFilter(request, response);
	}

	@Test
	void doFilter_invalidToken_doesNotSetAuthentication() throws Exception {
		UserDetails user = mock(UserDetails.class);
		when(user.getUsername()).thenReturn("ana99");
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer invalid-token");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);

		when(jwtService.extractUsername("invalid-token")).thenReturn("ana99");
		when(userDetailsService.loadUserByUsername("ana99")).thenReturn(user);
		when(jwtService.isValid("invalid-token", user)).thenReturn(false);

		filter.doFilter(request, response, chain);

		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
		verify(chain).doFilter(request, response);
	}

	@Test
	void doFilter_alreadyAuthenticated_doesNotReloadUser() throws Exception {
		SecurityContextHolder.getContext()
			.setAuthentication(new UsernamePasswordAuthenticationToken("ana99", null, List.of()));
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer some-token");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);

		when(jwtService.extractUsername("some-token")).thenReturn("ana99");

		filter.doFilter(request, response, chain);

		verify(userDetailsService, never()).loadUserByUsername("ana99");
		verify(chain).doFilter(request, response);
	}

}
