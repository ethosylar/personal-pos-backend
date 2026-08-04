package site.ethosylar.pos.staff.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.staff.entity.StaffSalaryRateEntity;

public interface StaffSalaryRateRepository extends JpaRepository<StaffSalaryRateEntity, Long> {

	List<StaffSalaryRateEntity> findByStaff_IdOrderByEffectiveFromDesc(Long staffId);

	Optional<StaffSalaryRateEntity> findFirstByStaff_IdAndEffectiveToIsNull(Long staffId);

	Optional<StaffSalaryRateEntity> findByIdAndStaff_Id(Long id, Long staffId);
}