package site.ethosylar.pos.staff.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.staff.entity.StaffStatusEntity;

public interface StaffStatusRepository extends JpaRepository<StaffStatusEntity, Long> {

	Optional<StaffStatusEntity> findByCodeIgnoreCase(String code);

	List<StaffStatusEntity> findByActiveTrueOrderByDisplayOrderAscNameAsc();
}
