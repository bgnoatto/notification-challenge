package bgn.source.notification;

import bgn.source.notification.dto.CreateNotificationRequest;
import java.util.UUID;
import bgn.source.notification.dto.UpdateNotificationRequest;
import bgn.source.notification.model.Notification;
import bgn.source.notification.model.NotificationChannel;
import bgn.source.notification.model.User;
import bgn.source.notification.repository.NotificationRepository;
import bgn.source.notification.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest extends BaseIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void getOwn_returnsOwnNotifications() throws Exception {
		User user = savedUser("Ana", "ana99", "ana@test.com");
		savedNotification("Alerta", "Contenido" + NotificationChannel.EMAIL.getLabel(), NotificationChannel.EMAIL,
				user);

		mockMvc.perform(get("/notifications").with(user(user)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(1))
			.andExpect(jsonPath("$[0].title").value("Alerta"))
			.andExpect(jsonPath("$[0].userName").value("ana99"));
	}

	@Test
	void getOwn_doesNotReturnOthersNotifications() throws Exception {
		User ana = savedUser("Ana", "ana99", "ana@test.com");
		User bob = savedUser("Bob", "bob99", "bob@test.com");
		savedNotification("De Bob", "Contenido" + NotificationChannel.SMS.getLabel(), NotificationChannel.SMS, bob);

		mockMvc.perform(get("/notifications").with(user(ana)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void createNotification_returnsCreated() throws Exception {
		User user = savedUser("Ana", "ana99", "ana@test.com");
		String body = objectMapper.writeValueAsString(
				new CreateNotificationRequest("Alerta", "Contenido" + NotificationChannel.EMAIL.getLabel(), 1));

		mockMvc.perform(post("/notifications").with(user(user)).contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.title").value("Alerta"))
			.andExpect(jsonPath("$.channelCode").value(1))
			.andExpect(jsonPath("$.channelLabel").value("EMAIL"))
			.andExpect(jsonPath("$.userName").value("ana99"));
	}

	@Test
	void createNotification_invalidChannel_returnsBadRequest() throws Exception {
		User user = savedUser("Ana", "ana99", "ana@test.com");
		String body = objectMapper.writeValueAsString(new CreateNotificationRequest("Alerta",
				"Contenido" + NotificationChannel.EMAIL.getLabel(), Integer.MAX_VALUE));

		mockMvc.perform(post("/notifications").with(user(user)).contentType(MediaType.APPLICATION_JSON).content(body))
			.andExpect(status().isBadRequest());
	}

	@Test
	void updateNotification_returnsUpdated() throws Exception {
		User user = savedUser("Ana", "ana99", "ana@test.com");
		Notification notification = savedNotification("Original", "Contenido" + NotificationChannel.EMAIL.getLabel(),
				NotificationChannel.EMAIL, user);
		String body = objectMapper
			.writeValueAsString(new UpdateNotificationRequest("Actualizada", "Nuevo contenido", 2));

		mockMvc
			.perform(put("/notifications/{id}", notification.getId()).with(user(user))
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("Actualizada"))
			.andExpect(jsonPath("$.channelCode").value(2));
	}

	@Test
	void updateNotification_notOwner_returnsForbidden() throws Exception {
		User owner = savedUser("Ana", "ana99", "ana@test.com");
		User other = savedUser("Bob", "bob99", "bob@test.com");
		Notification notification = savedNotification("De Ana", "Contenido" + NotificationChannel.EMAIL.getLabel(),
				NotificationChannel.EMAIL, owner);
		String body = objectMapper.writeValueAsString(
				new UpdateNotificationRequest("Hackeada", "Contenido" + NotificationChannel.EMAIL.getLabel(), 1));

		mockMvc
			.perform(put("/notifications/{id}", notification.getId()).with(user(other))
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
			.andExpect(status().isForbidden());
	}

	@Test
	void updateNotification_notFound_returns404() throws Exception {
		User user = savedUser("Ana", "ana99", "ana@test.com");
		String body = objectMapper.writeValueAsString(
				new UpdateNotificationRequest("Ghost", "Contenido" + NotificationChannel.EMAIL.getLabel(), 1));

		mockMvc
			.perform(put("/notifications/{id}", Long.MAX_VALUE).with(user(user))
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
			.andExpect(status().isNotFound());
	}

	@Test
	void deleteNotification_returnsNoContent() throws Exception {
		User user = savedUser("Ana", "ana99", "ana@test.com");
		Notification notification = savedNotification("Para borrar", "Contenido " + NotificationChannel.PUSH.getLabel(),
				NotificationChannel.PUSH, user);

		mockMvc.perform(delete("/notifications/{id}", notification.getId()).with(user(user)))
			.andExpect(status().isNoContent());
	}

	@Test
	void deleteNotification_notOwner_returnsForbidden() throws Exception {
		User owner = savedUser("Ana", "ana99", "ana@test.com");
		User other = savedUser("Bob", "bob99", "bob@test.com");
		Notification notification = savedNotification("De Ana", "Contenido" + NotificationChannel.EMAIL.getLabel(),
				NotificationChannel.EMAIL, owner);

		mockMvc.perform(delete("/notifications/{id}", notification.getId()).with(user(other)))
			.andExpect(status().isForbidden());
	}

	@Test
	void deleteNotification_notFound_returns404() throws Exception {
		User user = savedUser("Ana", "ana99", "ana@test.com");

		mockMvc.perform(delete("/notifications/{id}", Long.MAX_VALUE).with(user(user)))
			.andExpect(status().isNotFound());
	}

	private User savedUser(String name, String userName, String email) {
		User user = new User();
		user.setName(name);
		user.setLastName("TestLastName");
		user.setUserName(userName);
		user.setEmail(email);
		user.setPhone("+5491100000000");
		user.setDeviceToken(UUID.randomUUID().toString());
		user.setPassword(passwordEncoder.encode("testpass"));
		return userRepository.save(user);
	}

	private Notification savedNotification(String title, String content, NotificationChannel channel, User user) {
		Notification notification = new Notification();
		notification.setTitle(title);
		notification.setContent(content);
		notification.setChannel(channel);
		notification.setUser(user);
		return notificationRepository.save(notification);
	}

}
