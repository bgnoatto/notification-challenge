package bgn.source.notification.service;

import bgn.source.notification.dto.AuthResponse;
import bgn.source.notification.dto.LoginRequest;
import bgn.source.notification.model.User;
import bgn.source.notification.repository.UserRepository;
import bgn.source.notification.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthService(
      UserRepository userRepository,
      JwtService jwtService,
      AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
  }

  public AuthResponse login(LoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.userName(), request.password()));
    User user =
        userRepository
            .findByUserName(request.userName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    return new AuthResponse(jwtService.generateToken(user));
  }
}
