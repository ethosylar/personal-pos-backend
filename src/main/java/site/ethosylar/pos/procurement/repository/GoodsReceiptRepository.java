package site.ethosylar.pos.procurement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.procurement.entity.GoodsReceiptEntity;

public interface GoodsReceiptRepository extends JpaRepository<GoodsReceiptEntity, Long> {

	List<GoodsReceiptEntity> findByPurchaseOrder_IdOrderByReceivedAtDesc(Long purchaseOrderId);

	boolean existsByReceiptNumberIgnoreCase(String receiptNumber);

	boolean existsByPurchaseOrder_Id(Long purchaseOrderId);
}
