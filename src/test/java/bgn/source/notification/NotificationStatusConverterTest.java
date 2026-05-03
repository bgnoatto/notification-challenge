package bgn.source.notification;

import static org.assertj.core.api.Assertions.assertThat;

import bgn.source.notification.model.NotificationStatus;
import bgn.source.notification.model.NotificationStatusConverter;
import org.junit.jupiter.api.Test;

class NotificationStatusConverterTest {

	private final NotificationStatusConverter converter = new NotificationStatusConverter();

	@Test
	void convertToDatabaseColumn_null_returnsNull() {
		assertThat(converter.convertToDatabaseColumn(null)).isNull();
	}

	@Test
	void convertToDatabaseColumn_validStatus_returnsCode() {
		assertThat(converter.convertToDatabaseColumn(NotificationStatus.SENDING)).isEqualTo(1);
		assertThat(converter.convertToDatabaseColumn(NotificationStatus.SENT)).isEqualTo(2);
		assertThat(converter.convertToDatabaseColumn(NotificationStatus.FAILED)).isEqualTo(3);
	}

	@Test
	void convertToEntityAttribute_null_returnsNull() {
		assertThat(converter.convertToEntityAttribute(null)).isNull();
	}

	@Test
	void convertToEntityAttribute_validCode_returnsStatus() {
		assertThat(converter.convertToEntityAttribute(1)).isEqualTo(NotificationStatus.SENDING);
		assertThat(converter.convertToEntityAttribute(2)).isEqualTo(NotificationStatus.SENT);
		assertThat(converter.convertToEntityAttribute(3)).isEqualTo(NotificationStatus.FAILED);
	}

}
