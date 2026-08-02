package site.ethosylar.pos.procurement.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import site.ethosylar.pos.procurement.entity.GoodsReceiptEntity;
import site.ethosylar.pos.procurement.entity.GoodsReceiptItemEntity;
import site.ethosylar.pos.procurement.entity.PurchaseOrderEntity;
import site.ethosylar.pos.procurement.entity.PurchaseOrderItemEntity;
import site.ethosylar.pos.procurement.response.GoodsReceiptItemResponse;
import site.ethosylar.pos.procurement.response.GoodsReceiptResponse;
import site.ethosylar.pos.procurement.response.PurchaseOrderItemResponse;
import site.ethosylar.pos.procurement.response.PurchaseOrderResponse;

@Component
public class ProcurementMapper {

	public PurchaseOrderItemResponse toPurchaseOrderItemResponse(PurchaseOrderItemEntity item, BigDecimal received,
			BigDecimal remaining) {

		return PurchaseOrderItemResponse.builder().id(item.getId())

				.ingredientId(item.getIngredient().getId())

				.ingredientCode(item.getIngredient().getCode())

				.ingredientName(item.getIngredient().getName())

				.purchaseUnitId(item.getPurchaseUnit().getId())

				.purchaseUnitCode(item.getPurchaseUnit().getCode())

				.purchaseUnitSymbol(item.getPurchaseUnit().getSymbol())

				.orderedQuantity(item.getOrderedQuantity())

				.receivedQuantity(received)

				.remainingQuantity(remaining)

				.baseQuantityPerUnit(item.getBaseQuantityPerUnit())

				.unitPrice(item.getUnitPrice())

				.lineSubtotal(item.getLineSubtotal())

				.supplierItemCode(item.getSupplierItemCode())

				.notes(item.getNotes())

				.build();
	}

	public static PurchaseOrderResponse toPurchaseOrderResponse(PurchaseOrderEntity po,
			List<PurchaseOrderItemResponse> items) {

		return PurchaseOrderResponse.builder().id(po.getId())

				.poNumber(po.getPoNumber())

				.supplierId(po.getSupplier().getId())

				.supplierCode(po.getSupplier().getCode())

				.supplierName(po.getSupplier().getName())

				.statusId(po.getStatus().getId())

				.statusCode(po.getStatus().getCode())

				.statusName(po.getStatus().getName())

				.orderDate(po.getOrderDate())

				.expectedDeliveryDate(po.getExpectedDeliveryDate())

				.subtotal(po.getSubtotal())

				.taxAmount(po.getTaxAmount())

				.deliveryFee(po.getDeliveryFee())

				.totalAmount(po.getTotalAmount())

				.notes(po.getNotes())

				.items(items)

				.createdByUserId(po.getCreatedByUser() != null ? po.getCreatedByUser().getId() : null)

				.createdByUsername(po.getCreatedByUser() != null ? po.getCreatedByUser().getUsername() : null)

				.createdAt(po.getCreatedAt())

				.updatedAt(po.getUpdatedAt())

				.build();
	}

	public GoodsReceiptItemResponse toGoodsReceiptItemResponse(GoodsReceiptItemEntity item) {

		PurchaseOrderItemEntity poItem = item.getPurchaseOrderItem();

		return GoodsReceiptItemResponse.builder().id(item.getId())

				.purchaseOrderItemId(poItem.getId())

				.ingredientId(poItem.getIngredient().getId())

				.ingredientCode(poItem.getIngredient().getCode())

				.ingredientName(poItem.getIngredient().getName())

				.receivedQuantity(item.getReceivedQuantity())

				.purchaseUnitCode(poItem.getPurchaseUnit().getCode())

				.baseQuantityPerUnit(item.getBaseQuantityPerUnit())

				.receivedBaseQuantity(item.getReceivedBaseQuantity())

				.baseUnitCode(poItem.getIngredient().getBaseUnit().getCode())

				.baseUnitSymbol(poItem.getIngredient().getBaseUnit().getSymbol())

				.unitPrice(item.getUnitPrice())

				.baseUnitCost(item.getBaseUnitCost())

				.notes(item.getNotes())

				.build();
	}

	public GoodsReceiptResponse toGoodsReceiptResponse(GoodsReceiptEntity receipt,
			List<GoodsReceiptItemResponse> items) {

		return GoodsReceiptResponse.builder().id(receipt.getId())

				.receiptNumber(receipt.getReceiptNumber())

				.purchaseOrderId(receipt.getPurchaseOrder().getId())

				.poNumber(receipt.getPurchaseOrder().getPoNumber())

				.receivedAt(receipt.getReceivedAt())

				.supplierDeliveryNo(receipt.getSupplierDeliveryNo())

				.notes(receipt.getNotes())

				.receivedByUserId(receipt.getReceivedByUser() != null ? receipt.getReceivedByUser().getId() : null)

				.receivedByUsername(
						receipt.getReceivedByUser() != null ? receipt.getReceivedByUser().getUsername() : null)

				.items(items)

				.createdAt(receipt.getCreatedAt())

				.build();
	}
}