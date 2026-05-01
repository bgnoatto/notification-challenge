package bgn.source.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bgn.source.notification.dto.CreateUserRequest;
import bgn.source.notification.dto.PatchUserRequest;
import bgn.source.notification.dto.UpdateUserRequest;
import bgn.source.notification.dto.UserResponse;
import bgn.source.notification.model.User;
import bgn.source.notification.repository.UserRepository;
import bgn.source.notification.service.UserService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

class UserServiceTest {

	private final UserRepository userRepository = mock(UserRepository.class);

	private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

	private final UserService userService = new UserService(userRepository, passwordEncoder);

	@Test
	void getAllUsers_returnsMappedList() {
		when(userRepository.findAll()).thenReturn(List.of(buildUser()));

		List<UserResponse> result = userService.getAllUsers();

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().email()).isEqualTo("ana@test.com");
	}

	@Test
	void getUserById_found_returnsResponse() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser()));

		UserResponse result = userService.getUserById(1L);

		assertThat(result.email()).isEqualTo("ana@test.com");
	}

	@Test
	void getUserById_notFound_throws404() {
		when(userRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.getUserById(Long.MAX_VALUE)).isInstanceOf(ResponseStatusException.class);
	}

	@Test
	void createUser_emailConflict_throws409() {
		when(userRepository.existsByEmail("ana@test.com")).thenReturn(true);

		assertThatThrownBy(() -> userService.createUser(createRequest())).isInstanceOf(ResponseStatusException.class);
	}

	@Test
	void createUser_success_encodesPasswordAndSaves() {
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(passwordEncoder.encode("pass")).thenReturn("hashed");
		when(userRepository.save(any())).thenReturn(buildUser());

		UserResponse result = userService.createUser(createRequest());

		assertThat(result.email()).isEqualTo("ana@test.com");
		verify(passwordEncoder).encode("pass");
	}

	@Test
	void updateUser_notFound_throws404() {
		when(userRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.updateUser(Long.MAX_VALUE, updateRequest()))
			.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	void updateUser_emailConflict_throws409() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser()));
		when(userRepository.existsByEmailAndIdNot("ana@test.com", 1L)).thenReturn(true);

		assertThatThrownBy(() -> userService.updateUser(1L, updateRequest()))
			.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	void updateUser_success_updatesUser() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser()));
		when(userRepository.existsByEmailAndIdNot(anyString(), anyLong())).thenReturn(false);
		when(passwordEncoder.encode("pass")).thenReturn("hashed");
		when(userRepository.save(any())).thenReturn(buildUser());

		UserResponse result = userService.updateUser(1L, updateRequest());

		assertThat(result.email()).isEqualTo("ana@test.com");
	}

	@Test
	void patchUser_notFound_throws404() {
		when(userRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

		assertThatThrownBy(
				() -> userService.patchUser(Long.MAX_VALUE, new PatchUserRequest(null, null, null, null, null, null)))
			.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	void patchUser_emailConflict_throws409() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser()));
		when(userRepository.existsByEmailAndIdNot("ana@test.com", 1L)).thenReturn(true);

		assertThatThrownBy(
				() -> userService.patchUser(1L, new PatchUserRequest(null, null, "ana@test.com", null, null, null)))
			.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	void patchUser_allFieldsNull_savesUnchanged() {
		User user = buildUser();
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(userRepository.save(any())).thenReturn(user);

		userService.patchUser(1L, new PatchUserRequest(null, null, null, null, null, null));

		verify(userRepository).save(user);
	}

	@Test
	void patchUser_allFieldsPresent_updatesAll() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser()));
		when(userRepository.existsByEmailAndIdNot(anyString(), anyLong())).thenReturn(false);
		when(passwordEncoder.encode("newpass")).thenReturn("newhashed");
		when(userRepository.save(any())).thenReturn(buildUser());

		userService.patchUser(1L,
				new PatchUserRequest("NewName", "NewLast", "new@test.com", "+5491199999999", "new-token", "newpass"));

		verify(passwordEncoder).encode("newpass");
		verify(userRepository).save(any());
	}

	@Test
	void deleteUser_notFound_throws404() {
		when(userRepository.existsById(Long.MAX_VALUE)).thenReturn(false);

		assertThatThrownBy(() -> userService.deleteUser(Long.MAX_VALUE)).isInstanceOf(ResponseStatusException.class);
	}

	@Test
	void deleteUser_success_deletesById() {
		when(userRepository.existsById(1L)).thenReturn(true);

		userService.deleteUser(1L);

		verify(userRepository).deleteById(1L);
	}

	private User buildUser() {
		User user = new User();
		user.setName("Ana");
		user.setLastName("Lopez");
		user.setUserName("ana99");
		user.setEmail("ana@test.com");
		user.setPhone("+5491100000000");
		user.setDeviceToken("token-abc");
		user.setPassword("hashed");
		return user;
	}

	private CreateUserRequest createRequest() {
		return new CreateUserRequest("Ana", "Lopez", "ana99", "ana@test.com", "+5491100000000", "token-abc", "pass");
	}

	private UpdateUserRequest updateRequest() {
		return new UpdateUserRequest("Ana", "Lopez", "ana@test.com", "+5491100000000", "token-abc", "pass");
	}

}
