package site.ethosylar.pos.common.outbox.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.outbox.OutboxStatuses;
import site.ethosylar.pos.common.outbox.entity.OutboxEventEntity;
import site.ethosylar.pos.common.outbox.repository.OutboxEventRepository;

@Service
@RequiredArgsConstructor
public class OutboxClaimService {

	private final OutboxEventRepository outboxEventRepository;

	@Transactional
	public List<Long> claimBatch(int requestedLimit, String processorId) {

		int safeLimit = Math.min(Math.max(requestedLimit, 1), 100);

		List<OutboxEventEntity> events = outboxEventRepository.findClaimableEvents(safeLimit);

		OffsetDateTime now = OffsetDateTime.now();

		for (OutboxEventEntity event : events) {

			event.setStatus(OutboxStatuses.PROCESSING);

			event.setAttempts(event.getAttempts() + 1);

			event.setClaimedAt(now);
			event.setClaimedBy(processorId);
			event.setUpdatedAt(now);
		}

		outboxEventRepository.flush();

		return events.stream().map(OutboxEventEntity::getId).toList();
	}

	@Transactional
	public int releaseStaleClaims() {

		OffsetDateTime now = OffsetDateTime.now();

		OffsetDateTime cutoff = now.minusMinutes(5);

		return outboxEventRepository.releaseStaleProcessing(OutboxStatuses.PROCESSING, OutboxStatuses.PENDING, cutoff,
				now);
	}
}