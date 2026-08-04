package site.ethosylar.pos.staff.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.staff.entity.SalaryPayTypeEntity;

public interface SalaryPayTypeRepository extends JpaRepository<SalaryPayTypeEntity, Long> {

	Optional<SalaryPayTypeEntity> findByCodeIgnoreCase(String code);

	List<SalaryPayTypeEntity> findByActiveTrueOrderByDisplayOrderAscNameAsc();
}