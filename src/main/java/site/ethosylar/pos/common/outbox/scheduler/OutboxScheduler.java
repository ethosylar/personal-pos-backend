package site.ethosylar.pos.common.outbox.scheduler;

import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.outbox.service.OutboxClaimService;
import site.ethosylar.pos.common.outbox.service.OutboxWorkerService;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {

	private final OutboxWorkerService outboxWorkerService;

	private final OutboxClaimService outboxClaimService;

	private final String processorId = "POS-" + UUID.randomUUID();

	@Scheduled(fixedDelayString = "${app.outbox.poll-delay-ms:5000}")
	public void processOutbox() {

		outboxClaimService.releaseStaleClaims();

		outboxWorkerService.processBatch(processorId, 25);
	}
}