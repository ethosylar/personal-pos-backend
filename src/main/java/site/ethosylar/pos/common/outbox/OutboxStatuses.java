package site.ethosylar.pos.common.outbox;

public final class OutboxStatuses {

	public static final String PENDING = "PENDING";

	public static final String PROCESSING = "PROCESSING";

	public static final String PUBLISHED = "PUBLISHED";

	public static final String FAILED = "FAILED";

	private OutboxStatuses() {
	}
}