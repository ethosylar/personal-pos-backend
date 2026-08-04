package site.ethosylar.pos.staff.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import site.ethosylar.pos.staff.entity.StaffEntity;

public interface StaffRepository extends JpaRepository<StaffEntity, Long>, JpaSpecificationExecutor<StaffEntity> {

	Optional<StaffEntity> findByStaffCodeIgnoreCase(String staffCode);

	boolean existsByStaffCodeIgnoreCase(String staffCode);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			SELECT s
			FROM StaffEntity s
			WHERE s.id = :id
			""")
	Optional<StaffEntity> findByIdForUpdate(@Param("id") Long id);

	boolean existsByStaffCodeIgnoreCaseAndIdNot(String staffCode, Long id);

	boolean existsByUser_Id(Long userId);

	boolean existsByUser_IdAndIdNot(Long userId, Long id);
}
