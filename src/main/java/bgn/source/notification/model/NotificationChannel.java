package bgn.source.notification.model;

public enum NotificationChannel {

	EMAIL(1, "EMAIL"), SMS(2, "SMS"), PUSH(3, "PUSH");

	private final int code;

	private final String label;

	NotificationChannel(int code, String label) {
		this.code = code;
		this.label = label;
	}

	public static NotificationChannel fromCode(int code) {
		for (NotificationChannel c : values()) {
			if (c.code == code) {
				return c;
			}
		}
		throw new IllegalArgumentException("Invalid channel code: " + code);
	}

	public int getCode() {
		return code;
	}

	public String getLabel() {
		return label;
	}

}
