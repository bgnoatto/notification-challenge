package bgn.source.notification.controller;

import bgn.source.notification.dto.UserRequest;
import bgn.source.notification.dto.UserResponse;
import bgn.source.notification.service.UserService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<List<UserResponse>> getAll() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @PostMapping
  public ResponseEntity<UserResponse> create(@RequestBody UserRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserResponse> update(
      @PathVariable Long id, @RequestBody UserRequest request) {
    return ResponseEntity.ok(userService.updateUser(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
