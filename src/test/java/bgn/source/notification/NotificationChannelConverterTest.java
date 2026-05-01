package bgn.source.notification;

import static org.assertj.core.api.Assertions.assertThat;

import bgn.source.notification.model.NotificationChannel;
import bgn.source.notification.model.NotificationChannelConverter;
import org.junit.jupiter.api.Test;

class NotificationChannelConverterTest {

	private final NotificationChannelConverter converter = new NotificationChannelConverter();

	@Test
	void convertToDatabaseColumn_null_returnsNull() {
		assertThat(converter.convertToDatabaseColumn(null)).isNull();
	}

	@Test
	void convertToDatabaseColumn_validChannel_returnsCode() {
		assertThat(converter.convertToDatabaseColumn(NotificationChannel.EMAIL)).isEqualTo(1);
	}

	@Test
	void convertToEntityAttribute_null_returnsNull() {
		assertThat(converter.convertToEntityAttribute(null)).isNull();
	}

	@Test
	void convertToEntityAttribute_validCode_returnsChannel() {
		assertThat(converter.convertToEntityAttribute(2)).isEqualTo(NotificationChannel.SMS);
	}

}
