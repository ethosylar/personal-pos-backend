package site.ethosylar.pos.sale.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import site.ethosylar.pos.sale.entity.SaleEntity;

public interface SaleRepository extends JpaRepository<SaleEntity, Long>, JpaSpecificationExecutor<SaleEntity> {

	Optional<SaleEntity> findBySaleNumberIgnoreCase(String saleNumber);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			SELECT s
			FROM SaleEntity s
			WHERE s.id = :id
			""")
	Optional<SaleEntity> findByIdForUpdate(@Param("id") Long id);
}
