package site.ethosylar.pos.common.outbox.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.outbox.entity.OutboxEventEntity;
import site.ethosylar.pos.common.outbox.publisher.OutboxPublisher;

@Service
@RequiredArgsConstructor
public class OutboxWorkerService {

	private static final Logger log = LoggerFactory.getLogger(OutboxWorkerService.class);

	private final OutboxClaimService outboxClaimService;

	private final OutboxProcessingService outboxProcessingService;

	private final OutboxPublisher outboxPublisher;

	public void processBatch(String processorId, int batchSize) {

		List<Long> eventIds = outboxClaimService.claimBatch(batchSize, processorId);

		for (Long eventId : eventIds) {

			processOne(eventId, processorId);
		}
	}

	private void processOne(Long eventId, String processorId) {

		try {

			OutboxEventEntity event = outboxProcessingService.getClaimedEvent(eventId, processorId)
					.orElseThrow(() -> new IllegalStateException("Claimed event " + "not found: " + eventId));

			outboxPublisher.publish(event);

			outboxProcessingService.markPublished(eventId, processorId);

		} catch (Exception ex) {

			log.error("Outbox processing failed " + "for eventId={}", eventId, ex);

			try {

				outboxProcessingService.markFailed(eventId, processorId, ex);

			} catch (Exception markFailure) {

				log.error("Failed to record " + "outbox failure " + "for eventId={}", eventId, markFailure);
			}
		}
	}
}