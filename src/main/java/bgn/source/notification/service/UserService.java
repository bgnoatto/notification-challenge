package bgn.source.notification.service;

import bgn.source.notification.dto.CreateUserRequest;
import bgn.source.notification.dto.UpdateUserRequest;
import bgn.source.notification.dto.UserResponse;
import bgn.source.notification.model.User;
import bgn.source.notification.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public List<UserResponse> getAllUsers() {
		return userRepository.findAll().stream().map(UserResponse::from).toList();
	}

	public UserResponse getUserById(Long id) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
		return UserResponse.from(user);
	}

	public UserResponse createUser(CreateUserRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use: " + request.email());
		}
		User user = new User();
		user.setName(request.name());
		user.setLastName(request.lastName());
		user.setUserName(request.userName());
		user.setEmail(request.email());
		user.setPhone(request.phone());
		user.setDeviceToken(request.deviceToken());
		user.setPassword(passwordEncoder.encode(request.password()));
		return UserResponse.from(userRepository.save(user));
	}

	public UserResponse updateUser(Long id, UpdateUserRequest request) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
		if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use: " + request.email());
		}
		user.setName(request.name());
		user.setLastName(request.lastName());
		user.setEmail(request.email());
		user.setPhone(request.phone());
		user.setDeviceToken(request.deviceToken());
		user.setPassword(passwordEncoder.encode(request.password()));
		return UserResponse.from(userRepository.save(user));
	}

	public void deleteUser(Long id) {
		if (!userRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id);
		}
		userRepository.deleteById(id);
	}

}
