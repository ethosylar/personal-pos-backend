package site.ethosylar.pos.common.outbox.publisher;

import site.ethosylar.pos.common.outbox.entity.OutboxEventEntity;

public interface OutboxPublisher {

	void publish(OutboxEventEntity event);
}