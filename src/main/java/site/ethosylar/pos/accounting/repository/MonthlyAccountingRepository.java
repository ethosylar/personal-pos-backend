package site.ethosylar.pos.accounting.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import site.ethosylar.pos.accounting.entity.MonthlyAccountingEntity;

public interface MonthlyAccountingRepository
		extends JpaRepository<MonthlyAccountingEntity, Long>, JpaSpecificationExecutor<MonthlyAccountingEntity> {

	Optional<MonthlyAccountingEntity> findByAccountingNumberIgnoreCase(String accountingNumber);

	Optional<MonthlyAccountingEntity> findFirstByPeriodYearAndPeriodMonthAndCurrentRecordTrue(Short periodYear,
			Short periodMonth);

	boolean existsByPeriodYearAndPeriodMonthAndCurrentRecordTrue(Short periodYear, Short periodMonth);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			SELECT a
			FROM MonthlyAccountingEntity a
			WHERE a.id = :id
			""")
	Optional<MonthlyAccountingEntity> findByIdForUpdate(@Param("id") Long id);

	boolean existsByPeriodYearAndPeriodMonthAndCurrentRecordTrueAndStatus_CodeIgnoreCase(Short periodYear,
			Short periodMonth, String statusCode);
}