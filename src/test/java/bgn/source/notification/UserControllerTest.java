package bgn.source.notification;

import bgn.source.notification.client.PokeApiClient;
import bgn.source.notification.dto.UserRequest;
import bgn.source.notification.model.User;
import bgn.source.notification.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
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

    @MockitoBean
    private PokeApiClient pokeApiClient;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        when(pokeApiClient.getPokemonName(anyInt())).thenReturn("pikachu");
    }

    @Test
    void createUser_returnsCreated() throws Exception {
        String body = toJson(new UserRequest("Ana", "ana@test.com", "pass", Set.of(1)));

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Ana"))
                .andExpect(jsonPath("$.email").value("ana@test.com"));
    }

    @Test
    void createUser_duplicateEmail_returnsConflict() throws Exception {
        String body = toJson(new UserRequest("Ana", "ana@test.com", "pass", Set.of()));

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body));
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void getUserById_returnsUserWithPokemons() throws Exception {
        User user = savedUser("Juan", "juan@test.com", new Integer[]{25, 4});

        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@test.com"))
                .andExpect(jsonPath("$.pokemons[0].name").value("pikachu"));
    }

    @Test
    void getUserById_pokemonNamesResolvedFromMock_notFromRealApi() throws Exception {
        when(pokeApiClient.getPokemonName(1)).thenReturn("bulbasaur");
        when(pokeApiClient.getPokemonName(4)).thenReturn("charmander");
        User user = savedUser("Ash", "ash@test.com", new Integer[]{1, 4});

        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pokemons[?(@.id == 1)].name").value("bulbasaur"))
                .andExpect(jsonPath("$.pokemons[?(@.id == 4)].name").value("charmander"));

        verify(pokeApiClient).getPokemonName(1);
        verify(pokeApiClient).getPokemonName(4);
    }

    @Test
    void getUserById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/users/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_returnsAllUsers() throws Exception {
        savedUser("Ana", "ana@test.com", new Integer[]{});
        savedUser("Bob", "bob@test.com", new Integer[]{});

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateUser_returnsUpdated() throws Exception {
        User user = savedUser("Old Name", "old@test.com", new Integer[]{});
        String body = toJson(new UserRequest("New Name", "new@test.com", "pass", Set.of()));

        mockMvc.perform(put("/users/{id}", user.getId()).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.email").value("new@test.com"));
    }

    @Test
    void updateUser_keepSameEmail_returnsOk() throws Exception {
        User user = savedUser("Ana", "ana@test.com", new Integer[]{});
        String body = toJson(new UserRequest("Ana Updated", "ana@test.com", "pass", Set.of()));

        mockMvc.perform(put("/users/{id}", user.getId()).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ana Updated"));
    }

    @Test
    void updateUser_emailTakenByOther_returnsConflict() throws Exception {
        savedUser("Ana", "ana@test.com", new Integer[]{});
        User bob = savedUser("Bob", "bob@test.com", new Integer[]{});
        String body = toJson(new UserRequest("Bob", "ana@test.com", "pass", Set.of()));

        mockMvc.perform(put("/users/{id}", bob.getId()).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void updateUser_notFound_returns404() throws Exception {
        String body = toJson(new UserRequest("Ghost", "ghost@test.com", "pass", Set.of()));

        mockMvc.perform(put("/users/{id}", Long.MAX_VALUE).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_returnsNoContent() throws Exception {
        User user = savedUser("Ana", "ana@test.com", new Integer[]{});

        mockMvc.perform(delete("/users/{id}", user.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/users/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    private User savedUser(String name, String email, Integer[] pokemonIds) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("testpass"));
        user.setIdPokemons(pokemonIds);
        return userRepository.save(user);
    }

    private String toJson(UserRequest req) throws Exception {
        return objectMapper.writeValueAsString(req);
    }
}
