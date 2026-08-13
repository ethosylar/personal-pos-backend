package site.ethosylar.pos.common.outbox.service;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.outbox.OutboxStatuses;
import site.ethosylar.pos.common.outbox.entity.OutboxEventEntity;
import site.ethosylar.pos.common.outbox.repository.OutboxEventRepository;

@Service
@RequiredArgsConstructor
public class OutboxProcessingService {

	private static final int MAX_ATTEMPTS = 5;

	private final OutboxEventRepository outboxEventRepository;

	@Transactional(readOnly = true)
	public Optional<OutboxEventEntity> getClaimedEvent(Long eventId, String processorId) {

		return outboxEventRepository.findByIdAndStatusAndClaimedBy(eventId, OutboxStatuses.PROCESSING, processorId);
	}

	@Transactional
	public void markPublished(Long eventId, String processorId) {

		OutboxEventEntity event = getOwnedProcessingEvent(eventId, processorId);

		OffsetDateTime now = OffsetDateTime.now();

		event.setStatus(OutboxStatuses.PUBLISHED);

		event.setPublishedAt(now);

		event.setClaimedAt(null);
		event.setClaimedBy(null);
		event.setNextAttemptAt(null);
		event.setLastError(null);
		event.setUpdatedAt(now);
	}

	@Transactional
	public void markFailed(Long eventId, String processorId, Exception exception) {

		OutboxEventEntity event = getOwnedProcessingEvent(eventId, processorId);

		OffsetDateTime now = OffsetDateTime.now();

		int attempts = event.getAttempts();

		event.setLastError(sanitizeError(exception));

		event.setClaimedAt(null);
		event.setClaimedBy(null);
		event.setUpdatedAt(now);

		if (attempts >= MAX_ATTEMPTS) {

			event.setStatus(OutboxStatuses.FAILED);

			event.setNextAttemptAt(null);

			return;
		}

		event.setStatus(OutboxStatuses.PENDING);

		event.setNextAttemptAt(now.plusSeconds(calculateRetryDelaySeconds(attempts)));
	}

	private OutboxEventEntity getOwnedProcessingEvent(Long eventId, String processorId) {

		return outboxEventRepository.findByIdAndStatusAndClaimedBy(eventId, OutboxStatuses.PROCESSING, processorId)
				.orElseThrow(
						() -> new IllegalStateException("Outbox event is not owned " + "by processor: " + eventId));
	}

	private long calculateRetryDelaySeconds(int attempts) {

		return switch (attempts) {
		case 1 -> 10;
		case 2 -> 30;
		case 3 -> 60;
		case 4 -> 300;
		default -> 900;
		};
	}

	private String sanitizeError(Exception exception) {

		String message = exception.getMessage();

		if (message == null || message.isBlank()) {

			message = exception.getClass().getSimpleName();
		}

		if (message.length() > 1000) {

			return message.substring(0, 1000);
		}

		return message;
	}
}