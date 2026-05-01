package bgn.source.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import bgn.source.notification.model.NotificationChannel;
import bgn.source.notification.service.NotificationSender;
import bgn.source.notification.service.NotificationSenderRegistry;
import java.util.List;
import org.junit.jupiter.api.Test;

class NotificationSenderRegistryTest {

	@Test
	void forChannel_registeredChannel_returnsSender() {
		NotificationSender emailSender = mock(NotificationSender.class);
		when(emailSender.supportedChannel()).thenReturn(NotificationChannel.EMAIL);
		NotificationSenderRegistry registry = new NotificationSenderRegistry(List.of(emailSender));

		assertThat(registry.forChannel(NotificationChannel.EMAIL)).isEqualTo(emailSender);
	}

	@Test
	void forChannel_unregisteredChannel_throwsIllegalState() {
		NotificationSenderRegistry registry = new NotificationSenderRegistry(List.of());

		assertThatThrownBy(() -> registry.forChannel(NotificationChannel.SMS))
			.isInstanceOf(IllegalStateException.class);
	}

}
