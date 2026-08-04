package site.ethosylar.pos.payroll.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import site.ethosylar.pos.payroll.entity.StaffMonthlyCostEntity;

public interface StaffMonthlyCostRepository
		extends JpaRepository<StaffMonthlyCostEntity, Long>, JpaSpecificationExecutor<StaffMonthlyCostEntity> {

	Optional<StaffMonthlyCostEntity> findByCostNumberIgnoreCase(String costNumber);

	boolean existsByStaff_IdAndPeriodYearAndPeriodMonthAndCurrentRecordTrue(Long staffId, Short periodYear,
			Short periodMonth);

	List<StaffMonthlyCostEntity> findByStaff_IdOrderByPeriodYearDescPeriodMonthDesc(Long staffId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			SELECT c
			FROM StaffMonthlyCostEntity c
			WHERE c.id = :id
			""")
	Optional<StaffMonthlyCostEntity> findByIdForUpdate(@Param("id") Long id);
}