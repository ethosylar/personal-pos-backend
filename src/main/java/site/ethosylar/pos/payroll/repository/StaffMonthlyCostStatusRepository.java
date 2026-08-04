package site.ethosylar.pos.payroll.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.payroll.entity.StaffMonthlyCostStatusEntity;

public interface StaffMonthlyCostStatusRepository extends JpaRepository<StaffMonthlyCostStatusEntity, Long> {

	Optional<StaffMonthlyCostStatusEntity> findByCodeIgnoreCase(String code);
}