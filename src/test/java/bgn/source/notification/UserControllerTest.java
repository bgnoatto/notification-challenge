package bgn.source.notification;

import bgn.source.notification.dto.CreateUserRequest;
import bgn.source.notification.dto.PatchUserRequest;
import bgn.source.notification.dto.UpdateUserRequest;
import bgn.source.notification.model.User;
import bgn.source.notification.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends BaseIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void createUser_returnsCreated() throws Exception {
		String body = objectMapper.writeValueAsString(new CreateUserRequest("Ana", "Garcia", "ana99", "ana@test.com",
				"+5491100000001", UUID.randomUUID().toString(), "pass"));

		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name").value("Ana"))
			.andExpect(jsonPath("$.userName").value("ana99"))
			.andExpect(jsonPath("$.email").value("ana@test.com"));
	}

	@Test
	void createUser_duplicateEmail_returnsConflict() throws Exception {
		String body = objectMapper.writeValueAsString(new CreateUserRequest("Ana", "Garcia", "ana99", "ana@test.com",
				"+5491100000001", UUID.randomUUID().toString(), "pass"));

		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body));
		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isConflict());
	}

	@Test
	@WithMockUser
	void getUserById_returnsOk() throws Exception {
		User user = savedUser("Juan", "juan99", "juan@test.com");

		mockMvc.perform(get("/users/{id}", user.getId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value("juan@test.com"))
			.andExpect(jsonPath("$.userName").value("juan99"));
	}

	@Test
	@WithMockUser
	void getUserById_notFound_returns404() throws Exception {
		mockMvc.perform(get("/users/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser
	void getAllUsers_returnsAllUsers() throws Exception {
		savedUser("Ana", "ana99", "ana@test.com");
		savedUser("Bob", "bob99", "bob@test.com");

		mockMvc.perform(get("/users")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
	}

	@Test
	@WithMockUser
	void updateUser_returnsUpdated() throws Exception {
		User user = savedUser("Old Name", "oldname99", "old@test.com");
		String body = objectMapper.writeValueAsString(new UpdateUserRequest("New Name", "Lopez", "new@test.com",
				"+5491100000002", UUID.randomUUID().toString(), "pass"));

		mockMvc.perform(put("/users/{id}", user.getId()).contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("New Name"))
			.andExpect(jsonPath("$.email").value("new@test.com"))
			.andExpect(jsonPath("$.userName").value("oldname99"));
	}

	@Test
	@WithMockUser
	void updateUser_keepSameEmail_returnsOk() throws Exception {
		User user = savedUser("Ana", "ana99", "ana@test.com");
		String body = objectMapper.writeValueAsString(new UpdateUserRequest("Ana Updated", "Garcia", "ana@test.com",
				"+5491100000001", UUID.randomUUID().toString(), "pass"));

		mockMvc.perform(put("/users/{id}", user.getId()).contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Ana Updated"))
			.andExpect(jsonPath("$.userName").value("ana99"));
	}

	@Test
	@WithMockUser
	void updateUser_emailTakenByOther_returnsConflict() throws Exception {
		savedUser("Ana", "ana99", "ana@test.com");
		User bob = savedUser("Bob", "bob99", "bob@test.com");
		String body = objectMapper.writeValueAsString(new UpdateUserRequest("Bob", "Smith", "ana@test.com",
				"+5491100000003", UUID.randomUUID().toString(), "pass"));

		mockMvc.perform(put("/users/{id}", bob.getId()).contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isConflict());
	}

	@Test
	@WithMockUser
	void updateUser_notFound_returns404() throws Exception {
		String body = objectMapper.writeValueAsString(new UpdateUserRequest("Ghost", "Rider", "ghost@test.com",
				"+5491100000004", UUID.randomUUID().toString(), "pass"));

		mockMvc.perform(put("/users/{id}", Long.MAX_VALUE).contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser
	void deleteUser_returnsNoContent() throws Exception {
		User user = savedUser("Ana", "ana99", "ana@test.com");

		mockMvc.perform(delete("/users/{id}", user.getId())).andExpect(status().isNoContent());
	}

	@Test
	@WithMockUser
	void deleteUser_notFound_returns404() throws Exception {
		mockMvc.perform(delete("/users/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser
	void patchUser_onlyPhone_updatesPhone() throws Exception {
		User user = savedUser("Ana", "ana99", "ana@test.com");
		String body = objectMapper
			.writeValueAsString(new PatchUserRequest(null, null, null, "+5491199999999", null, null));

		mockMvc.perform(patch("/users/{id}", user.getId()).contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.phone").value("+5491199999999"))
			.andExpect(jsonPath("$.name").value("Ana"))
			.andExpect(jsonPath("$.email").value("ana@test.com"));
	}

	@Test
	@WithMockUser
	void patchUser_onlyDeviceToken_updatesDeviceToken() throws Exception {
		User user = savedUser("Ana", "ana99", "ana@test.com");
		String newToken = UUID.randomUUID().toString();
		String body = objectMapper.writeValueAsString(new PatchUserRequest(null, null, null, null, newToken, null));

		mockMvc.perform(patch("/users/{id}", user.getId()).contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.deviceToken").value(newToken));
	}

	@Test
	@WithMockUser
	void patchUser_emailTaken_returnsConflict() throws Exception {
		savedUser("Ana", "ana99", "ana@test.com");
		User bob = savedUser("Bob", "bob99", "bob@test.com");
		String body = objectMapper
			.writeValueAsString(new PatchUserRequest(null, null, "ana@test.com", null, null, null));

		mockMvc.perform(patch("/users/{id}", bob.getId()).contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isConflict());
	}

	@Test
	@WithMockUser
	void patchUser_notFound_returns404() throws Exception {
		String body = objectMapper
			.writeValueAsString(new PatchUserRequest(null, null, null, "+5491199999999", null, null));

		mockMvc.perform(patch("/users/{id}", Long.MAX_VALUE).contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isNotFound());
	}

	private User savedUser(String name, String userName, String email) {
		User user = new User();
		user.setName(name);
		user.setLastName("TestLastName");
		user.setUserName(userName);
		user.setEmail(email);
		user.setPhone("+5491100000000");
		user.setDeviceToken(UUID.randomUUID().toString());
		user.setPassword(passwordEncoder.encode("testpass"));
		return userRepository.save(user);
	}

}
