package site.ethosylar.pos.common.outbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.common.outbox.entity.OutboxEventEntity;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, Long> {
}