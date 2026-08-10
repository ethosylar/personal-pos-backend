package site.ethosylar.pos.procurement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.procurement.entity.PurchaseOrderItemEntity;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItemEntity, Long> {

	@EntityGraph(attributePaths = { "ingredient" })
	List<PurchaseOrderItemEntity> findByPurchaseOrder_IdOrderByIdAsc(Long purchaseOrderId);

	Optional<PurchaseOrderItemEntity> findByIdAndPurchaseOrder_Id(Long id, Long purchaseOrderId);

	boolean existsByPurchaseOrder_IdAndIngredient_Id(Long purchaseOrderId, Long ingredientId);
}
