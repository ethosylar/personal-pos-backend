package site.ethosylar.pos.procurement.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.ethosylar.pos.procurement.entity.GoodsReceiptItemEntity;

public interface GoodsReceiptItemRepository extends JpaRepository<GoodsReceiptItemEntity, Long> {

	List<GoodsReceiptItemEntity> findByGoodsReceipt_IdOrderByIdAsc(Long goodsReceiptId);

	@Query("""
			SELECT COALESCE(
			    SUM(gri.receivedQuantity),
			    0
			)
			FROM GoodsReceiptItemEntity gri
			WHERE gri.purchaseOrderItem.id = :purchaseOrderItemId
			""")
	BigDecimal calculateReceivedQuantity(@Param("purchaseOrderItemId") Long purchaseOrderItemId);
}