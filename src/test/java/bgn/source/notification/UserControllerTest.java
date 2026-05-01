package bgn.source.notification;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bgn.source.notification.dto.UserRequest;
import bgn.source.notification.model.User;
import bgn.source.notification.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends BaseIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  void createUser_returnsCreated() throws Exception {
    String body = toJson(new UserRequest("Ana", "ana@test.com", "pass"));

    mockMvc
        .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Ana"))
        .andExpect(jsonPath("$.email").value("ana@test.com"));
  }

  @Test
  void createUser_duplicateEmail_returnsConflict() throws Exception {
    String body = toJson(new UserRequest("Ana", "ana@test.com", "pass"));

    mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body));
    mockMvc
        .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isConflict());
  }

  @Test
  @WithMockUser
  void getUserById_returnsOk() throws Exception {
    User user = savedUser("Juan", "juan@test.com");

    mockMvc
        .perform(get("/users/{id}", user.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("juan@test.com"));
  }

  @Test
  @WithMockUser
  void getUserById_notFound_returns404() throws Exception {
    mockMvc.perform(get("/users/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser
  void getAllUsers_returnsAllUsers() throws Exception {
    savedUser("Ana", "ana@test.com");
    savedUser("Bob", "bob@test.com");

    mockMvc
        .perform(get("/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  @WithMockUser
  void updateUser_returnsUpdated() throws Exception {
    User user = savedUser("Old Name", "old@test.com");
    String body = toJson(new UserRequest("New Name", "new@test.com", "pass"));

    mockMvc
        .perform(
            put("/users/{id}", user.getId()).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("New Name"))
        .andExpect(jsonPath("$.email").value("new@test.com"));
  }

  @Test
  @WithMockUser
  void updateUser_keepSameEmail_returnsOk() throws Exception {
    User user = savedUser("Ana", "ana@test.com");
    String body = toJson(new UserRequest("Ana Updated", "ana@test.com", "pass"));

    mockMvc
        .perform(
            put("/users/{id}", user.getId()).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Ana Updated"));
  }

  @Test
  @WithMockUser
  void updateUser_emailTakenByOther_returnsConflict() throws Exception {
    savedUser("Ana", "ana@test.com");
    User bob = savedUser("Bob", "bob@test.com");
    String body = toJson(new UserRequest("Bob", "ana@test.com", "pass"));

    mockMvc
        .perform(
            put("/users/{id}", bob.getId()).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isConflict());
  }

  @Test
  @WithMockUser
  void updateUser_notFound_returns404() throws Exception {
    String body = toJson(new UserRequest("Ghost", "ghost@test.com", "pass"));

    mockMvc
        .perform(
            put("/users/{id}", Long.MAX_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser
  void deleteUser_returnsNoContent() throws Exception {
    User user = savedUser("Ana", "ana@test.com");

    mockMvc.perform(delete("/users/{id}", user.getId())).andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser
  void deleteUser_notFound_returns404() throws Exception {
    mockMvc.perform(delete("/users/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
  }

  private User savedUser(String name, String email) {
    User user = new User();
    user.setName(name);
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode("testpass"));
    return userRepository.save(user);
  }

  private String toJson(UserRequest req) throws Exception {
    return objectMapper.writeValueAsString(req);
  }
}
