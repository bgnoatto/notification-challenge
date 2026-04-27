package bgn.source.notification_challenge.dto;

import bgn.source.notification_challenge.model.User;

import java.util.List;

public record UserDetailResponse(Long id, String name, String email, Integer[] idPokemons, List<PokemonEntry> pokemons) {

    public record PokemonEntry(Integer id, String name) {}

    public static UserDetailResponse from(User user, List<PokemonEntry> pokemons) {
        return new UserDetailResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getIdPokemons(),
                pokemons
        );
    }
}
