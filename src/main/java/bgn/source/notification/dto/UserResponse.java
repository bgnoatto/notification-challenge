package bgn.source.notification.dto;

import bgn.source.notification.model.User;

public record UserResponse(Long id, String name, String lastName, String userName, String email) {

	public static UserResponse from(User user) {
		return new UserResponse(user.getId(), user.getName(), user.getLastName(), user.getUserName(), user.getEmail());
	}
}
