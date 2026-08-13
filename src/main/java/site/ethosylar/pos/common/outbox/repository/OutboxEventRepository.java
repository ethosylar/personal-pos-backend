package site.ethosylar.pos.common.outbox.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.ethosylar.pos.common.outbox.entity.OutboxEventEntity;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, Long> {

	@Query(value = """
			SELECT *
			FROM dt_outbox_events_oe
			WHERE status = 'PENDING'
			  AND (
			        next_attempt_at IS NULL
			        OR next_attempt_at <= CURRENT_TIMESTAMP
			      )
			ORDER BY id
			FOR UPDATE SKIP LOCKED
			LIMIT :limit
			""", nativeQuery = true)
	List<OutboxEventEntity> findClaimableEvents(@Param("limit") int limit);

	List<OutboxEventEntity> findAllByIdIn(List<Long> ids);

	@Modifying
	@Query("""
			UPDATE OutboxEventEntity e
			   SET e.status = :pendingStatus,
			       e.claimedAt = null,
			       e.claimedBy = null,
			       e.updatedAt = :now
			 WHERE e.status = :processingStatus
			   AND e.claimedAt < :cutoff
			""")
	int releaseStaleProcessing(@Param("processingStatus") String processingStatus,
			@Param("pendingStatus") String pendingStatus, @Param("cutoff") OffsetDateTime cutoff,
			@Param("now") OffsetDateTime now);

	Optional<OutboxEventEntity> findByIdAndStatusAndClaimedBy(Long id, String status, String claimedBy);
}