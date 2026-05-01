package bgn.source.notification;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bgn.source.notification.dto.LoginRequest;
import bgn.source.notification.model.User;
import bgn.source.notification.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest extends BaseIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void login_validCredentials_returnsToken() throws Exception {
		User user = new User();
		user.setName("Ana");
		user.setLastName("Garcia");
		user.setUserName("ana99");
		user.setEmail("ana@test.com");
		user.setPassword(passwordEncoder.encode("pass"));
		userRepository.save(user);

		String body = objectMapper.writeValueAsString(new LoginRequest("ana99", "pass"));

		mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").isNotEmpty());
	}

	@Test
	void login_invalidCredentials_returnsUnauthorized() throws Exception {
		String body = objectMapper.writeValueAsString(new LoginRequest("ghost", "wrong"));

		mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isUnauthorized());
	}

}
