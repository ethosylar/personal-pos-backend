package site.ethosylar.pos.common.outbox.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import site.ethosylar.pos.common.outbox.entity.OutboxEventEntity;

@Component
public class LoggingOutboxPublisher implements OutboxPublisher {

	private static final Logger log = LoggerFactory.getLogger(LoggingOutboxPublisher.class);

	@Override
	public void publish(OutboxEventEntity event) {

		log.info(
				"Publishing outbox event " + "id={}, " + "eventId={}, " + "type={}, " + "aggregateType={}, "
						+ "aggregateId={}",
				event.getId(), event.getEventId(), event.getEventType(), event.getAggregateType(),
				event.getAggregateId());
	}
}