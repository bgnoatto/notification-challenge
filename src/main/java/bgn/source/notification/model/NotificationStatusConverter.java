package bgn.source.notification.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class NotificationStatusConverter implements AttributeConverter<NotificationStatus, Integer> {

	@Override
	public Integer convertToDatabaseColumn(NotificationStatus status) {
		return status == null ? null : status.getCode();
	}

	@Override
	public NotificationStatus convertToEntityAttribute(Integer code) {
		return code == null ? null : NotificationStatus.fromCode(code);
	}

}
