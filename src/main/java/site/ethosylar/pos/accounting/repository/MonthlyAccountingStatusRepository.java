package site.ethosylar.pos.accounting.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.accounting.entity.MonthlyAccountingStatusEntity;

public interface MonthlyAccountingStatusRepository extends JpaRepository<MonthlyAccountingStatusEntity, Long> {

	Optional<MonthlyAccountingStatusEntity> findByCodeIgnoreCase(String code);
}