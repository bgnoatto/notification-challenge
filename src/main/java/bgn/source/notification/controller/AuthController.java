package bgn.source.notification.controller;

import bgn.source.notification.dto.AuthResponse;
import bgn.source.notification.dto.LoginRequest;
import bgn.source.notification.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<Void> handleBadCredentials(BadCredentialsException ex) {
		LOGGER.error("Bad credentials {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}

}
