package site.ethosylar.pos.procurement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.procurement.entity.PurchaseOrderStatusEntity;

public interface PurchaseOrderStatusRepository extends JpaRepository<PurchaseOrderStatusEntity, Long> {

	Optional<PurchaseOrderStatusEntity> findByCodeIgnoreCase(String code);

	List<PurchaseOrderStatusEntity> findByActiveTrueOrderByDisplayOrderAscNameAsc();
}