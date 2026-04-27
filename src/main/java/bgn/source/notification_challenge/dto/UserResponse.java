package bgn.source.notification_challenge.dto;

import bgn.source.notification_challenge.model.User;

public record UserResponse(Long id, String name, String email, Integer[] idPokemons) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getIdPokemons());
    }
}
