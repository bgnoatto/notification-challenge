package bgn.source.notification.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class NotificationChannelConverter
    implements AttributeConverter<NotificationChannel, Integer> {

  @Override
  public Integer convertToDatabaseColumn(NotificationChannel channel) {
    return channel == null ? null : channel.getCode();
  }

  @Override
  public NotificationChannel convertToEntityAttribute(Integer code) {
    return code == null ? null : NotificationChannel.fromCode(code);
  }
}
