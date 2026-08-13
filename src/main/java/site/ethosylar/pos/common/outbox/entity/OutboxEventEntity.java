package site.ethosylar.pos.common.outbox.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dt_outbox_events_oe")
@Getter
@Setter
@NoArgsConstructor
public class OutboxEventEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "event_id", nullable = false, unique = true)
	private UUID eventId;

	@Column(name = "aggregate_type", nullable = false, length = 50)
	private String aggregateType;

	@Column(name = "aggregate_id")
	private Long aggregateId;

	@Column(name = "event_type", nullable = false, length = 100)
	private String eventType;

	@Column(name = "payload", nullable = false, columnDefinition = "text")
	private String payload;

	@Column(name = "status", nullable = false, length = 20)
	private String status;

	@Column(name = "attempts", nullable = false)
	private Integer attempts;

	@Column(name = "next_attempt_at")
	private OffsetDateTime nextAttemptAt;

	@Column(name = "occurred_at", nullable = false)
	private OffsetDateTime occurredAt;

	@Column(name = "published_at")
	private OffsetDateTime publishedAt;

	@Column(name = "last_error", columnDefinition = "text")
	private String lastError;

	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	private OffsetDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private OffsetDateTime updatedAt;

	@Column(name = "claimed_at")
	private OffsetDateTime claimedAt;

	@Column(name = "claimed_by", length = 100)
	private String claimedBy;
}