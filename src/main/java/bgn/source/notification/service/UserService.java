package bgn.source.notification.service;

import bgn.source.notification.client.PokeApiClient;
import bgn.source.notification.dto.UserDetailResponse;
import bgn.source.notification.dto.UserRequest;
import bgn.source.notification.dto.UserResponse;
import bgn.source.notification.model.User;
import bgn.source.notification.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PokeApiClient pokeApiClient;
  private final PasswordEncoder passwordEncoder;

  public UserService(
      UserRepository userRepository, PokeApiClient pokeApiClient, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.pokeApiClient = pokeApiClient;
    this.passwordEncoder = passwordEncoder;
  }

  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream().map(UserResponse::from).toList();
  }

  public UserDetailResponse getUserById(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));

    List<UserDetailResponse.PokemonEntry> pokemons =
        user.getIdPokemons() != null
            ? Arrays.stream(user.getIdPokemons())
                .map(
                    pokemonId ->
                        new UserDetailResponse.PokemonEntry(
                            pokemonId, pokeApiClient.getPokemonName(pokemonId)))
                .toList()
            : List.of();

    return UserDetailResponse.from(user, pokemons);
  }

  public UserResponse createUser(UserRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Email already in use: " + request.email());
    }
    User user = new User();
    user.setName(request.name());
    user.setEmail(request.email());
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setIdPokemons(toArray(request.idPokemons()));
    return UserResponse.from(userRepository.save(user));
  }

  public UserResponse updateUser(Long id, UserRequest request) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
    if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Email already in use: " + request.email());
    }
    user.setName(request.name());
    user.setEmail(request.email());
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setIdPokemons(toArray(request.idPokemons()));
    return UserResponse.from(userRepository.save(user));
  }

  private Integer[] toArray(java.util.Set<Integer> set) {
    return set != null ? set.toArray(Integer[]::new) : null;
  }

  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id);
    }
    userRepository.deleteById(id);
  }
}
