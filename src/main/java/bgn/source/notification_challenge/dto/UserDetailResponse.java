package bgn.source.notification_challenge.dto;

import bgn.source.notification_challenge.model.User;

import java.util.List;

public record UserDetailResponse(Long id, String name, String email, Integer[] idPokemons, List<String> pokemons) {

    public static UserDetailResponse from(User user, List<String> pokemonNames) {
        return new UserDetailResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getIdPokemons(),
                pokemonNames
        );
    }
}
