package site.ethosylar.pos.audit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import site.ethosylar.pos.audit.entity.AuditLogEntity;

public interface AuditLogRepository
        extends JpaRepository<AuditLogEntity, Long>,
                JpaSpecificationExecutor<AuditLogEntity> {
}