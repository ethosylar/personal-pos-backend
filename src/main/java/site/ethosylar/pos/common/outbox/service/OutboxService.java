package site.ethosylar.pos.common.outbox.service;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.outbox.OutboxStatuses;
import site.ethosylar.pos.common.outbox.entity.OutboxEventEntity;
import site.ethosylar.pos.common.outbox.repository.OutboxEventRepository;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class OutboxService {

	private final OutboxEventRepository outboxEventRepository;
	private final ObjectMapper objectMapper;

	public void record(String aggregateType, Long aggregateId, String eventType, Object payload) {

		OutboxEventEntity event = new OutboxEventEntity();

		event.setEventId(UUID.randomUUID());

		event.setAggregateType(aggregateType);

		event.setAggregateId(aggregateId);

		event.setEventType(eventType);

		event.setPayload(toJson(payload));

		event.setStatus(OutboxStatuses.PENDING);
		event.setAttempts(0);

		OffsetDateTime now = OffsetDateTime.now();

		event.setOccurredAt(now);
		event.setUpdatedAt(now);

		outboxEventRepository.save(event);
	}

	private String toJson(Object payload) {

		try {

			return objectMapper.writeValueAsString(payload);

		} catch (JsonParseException ex) {

			throw new IllegalStateException("Failed to serialize outbox payload", ex);
		}
	}
}