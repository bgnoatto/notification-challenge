package bgn.source.notification.controller;

import bgn.source.notification.dto.CreateNotificationRequest;
import bgn.source.notification.dto.NotificationResponse;
import bgn.source.notification.dto.UpdateNotificationRequest;
import bgn.source.notification.model.User;
import bgn.source.notification.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

	private final NotificationService notificationService;

	public NotificationController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@GetMapping
	public ResponseEntity<List<NotificationResponse>> getOwn(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(notificationService.getOwn(user));
	}

	@PostMapping
	public ResponseEntity<NotificationResponse> create(@RequestBody CreateNotificationRequest request,
			@AuthenticationPrincipal User user) {
		return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.create(request, user));
	}

	@PutMapping("/{id}")
	public ResponseEntity<NotificationResponse> update(@PathVariable Long id,
			@RequestBody UpdateNotificationRequest request, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(notificationService.update(id, request, user));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
		notificationService.delete(id, user);
		return ResponseEntity.noContent().build();
	}

}
