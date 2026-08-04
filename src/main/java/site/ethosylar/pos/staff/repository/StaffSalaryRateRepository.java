package site.ethosylar.pos.staff.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.ethosylar.pos.staff.entity.StaffSalaryRateEntity;

public interface StaffSalaryRateRepository extends JpaRepository<StaffSalaryRateEntity, Long> {

	List<StaffSalaryRateEntity> findByStaff_IdOrderByEffectiveFromDesc(Long staffId);

	Optional<StaffSalaryRateEntity> findFirstByStaff_IdAndEffectiveToIsNull(Long staffId);

	Optional<StaffSalaryRateEntity> findByIdAndStaff_Id(Long id, Long staffId);

	@Query("""
			SELECT r
			FROM StaffSalaryRateEntity r
			WHERE r.staff.id = :staffId
			  AND r.effectiveFrom <= :date
			  AND (
			        r.effectiveTo IS NULL
			        OR r.effectiveTo >= :date
			      )
			ORDER BY r.effectiveFrom DESC
			""")
	List<StaffSalaryRateEntity> findEffectiveRates(@Param("staffId") Long staffId,

			@Param("date") LocalDate date);
}