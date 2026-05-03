package bgn.source.notification.model;

public enum NotificationStatus {

	SENDING(1, "SENDING"), SENT(2, "SENT"), FAILED(3, "FAILED");

	private final int code;

	private final String label;

	NotificationStatus(int code, String label) {
		this.code = code;
		this.label = label;
	}

	public static NotificationStatus fromCode(int code) {
		for (NotificationStatus s : values()) {
			if (s.code == code) {
				return s;
			}
		}
		throw new IllegalArgumentException("Invalid status code: " + code);
	}

	public int getCode() {
		return code;
	}

	public String getLabel() {
		return label;
	}

}
