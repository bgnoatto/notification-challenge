package bgn.source.notification_challenge.service;

import bgn.source.notification_challenge.client.PokeApiClient;
import bgn.source.notification_challenge.dto.UserDetailResponse;
import bgn.source.notification_challenge.dto.UserResponse;
import bgn.source.notification_challenge.model.User;
import bgn.source.notification_challenge.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PokeApiClient pokeApiClient;

    public UserService(UserRepository userRepository, PokeApiClient pokeApiClient) {
        this.userRepository = userRepository;
        this.pokeApiClient = pokeApiClient;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserDetailResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        List<String> pokemonNames = user.getIdPokemons() != null
                ? Arrays.stream(user.getIdPokemons())
                        .map(pokeApiClient::getPokemonName)
                        .toList()
                : List.of();

        return UserDetailResponse.from(user, pokemonNames);
    }
}
