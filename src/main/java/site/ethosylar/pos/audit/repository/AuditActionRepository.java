package site.ethosylar.pos.audit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.audit.entity.AuditActionEntity;

public interface AuditActionRepository
        extends JpaRepository<AuditActionEntity, Long> {

    Optional<AuditActionEntity>
        findByCodeIgnoreCase(String code);
}