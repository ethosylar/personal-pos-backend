package site.ethosylar.pos.procurement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import site.ethosylar.pos.procurement.entity.PurchaseOrderEntity;

public interface PurchaseOrderRepository
		extends JpaRepository<PurchaseOrderEntity, Long>, JpaSpecificationExecutor<PurchaseOrderEntity> {

	Optional<PurchaseOrderEntity> findByPoNumberIgnoreCase(String poNumber);

	boolean existsByPoNumberIgnoreCase(String poNumber);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			SELECT po
			FROM PurchaseOrderEntity po
			WHERE po.id = :id
			""")
	Optional<PurchaseOrderEntity> findByIdForUpdate(@Param("id") Long id);
}
