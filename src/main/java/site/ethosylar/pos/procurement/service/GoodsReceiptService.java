package site.ethosylar.pos.procurement.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.request.AuditLogRequest;
import site.ethosylar.pos.audit.service.AuditService;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.inventory.service.InventoryPostingService;
import site.ethosylar.pos.procurement.entity.GoodsReceiptEntity;
import site.ethosylar.pos.procurement.entity.GoodsReceiptItemEntity;
import site.ethosylar.pos.procurement.entity.PurchaseOrderEntity;
import site.ethosylar.pos.procurement.entity.PurchaseOrderItemEntity;
import site.ethosylar.pos.procurement.entity.PurchaseOrderStatusEntity;
import site.ethosylar.pos.procurement.mapper.ProcurementMapper;
import site.ethosylar.pos.procurement.repository.GoodsReceiptItemRepository;
import site.ethosylar.pos.procurement.repository.GoodsReceiptRepository;
import site.ethosylar.pos.procurement.repository.PurchaseOrderItemRepository;
import site.ethosylar.pos.procurement.repository.PurchaseOrderRepository;
import site.ethosylar.pos.procurement.repository.PurchaseOrderStatusRepository;
import site.ethosylar.pos.procurement.request.CreateGoodsReceiptItemRequest;
import site.ethosylar.pos.procurement.request.CreateGoodsReceiptRequest;
import site.ethosylar.pos.procurement.response.GoodsReceiptItemResponse;
import site.ethosylar.pos.procurement.response.GoodsReceiptResponse;
import site.ethosylar.pos.user.entity.UserEntity;
import site.ethosylar.pos.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class GoodsReceiptService {

	private final PurchaseOrderRepository purchaseOrderRepository;

	private final PurchaseOrderItemRepository poItemRepository;

	private final PurchaseOrderStatusRepository statusRepository;

	private final GoodsReceiptRepository goodsReceiptRepository;

	private final GoodsReceiptItemRepository goodsReceiptItemRepository;

	private final ProcurementNumberService numberService;

	private final InventoryPostingService inventoryPostingService;

	private final UserRepository userRepository;

	private final AuditService auditService;

	private final ProcurementMapper procurementMapper;

	public GoodsReceiptEntity receive(Long purchaseOrderId, CreateGoodsReceiptRequest request) {

		PurchaseOrderEntity po = purchaseOrderRepository.findByIdForUpdate(purchaseOrderId)
				.orElseThrow(() -> new ResourceNotFoundException("Purchase order not found."));

		String status = po.getStatus().getCode();

		if (!status.equals("ORDERED") && !status.equals("PARTIALLY_RECEIVED")) {

			throw new BusinessRuleException(
					"Goods can only be received against an ordered or partially received purchase order.");
		}

		OffsetDateTime receivedAt = request.getReceivedAt() != null ? request.getReceivedAt() : OffsetDateTime.now();

		UserEntity currentUser = getCurrentUser();

		GoodsReceiptEntity receipt = new GoodsReceiptEntity();

		receipt.setReceiptNumber(numberService.nextGoodsReceiptNumber());

		receipt.setPurchaseOrder(po);

		receipt.setReceivedAt(receivedAt);

		receipt.setSupplierDeliveryNo(normalize(request.getSupplierDeliveryNo()));

		receipt.setNotes(normalize(request.getNotes()));

		receipt.setReceivedByUser(currentUser);

		receipt = goodsReceiptRepository.save(receipt);

		for (CreateGoodsReceiptItemRequest itemRequest : request.getItems()) {

			createReceiptItem(receipt, po, itemRequest, currentUser);
		}

		goodsReceiptItemRepository.flush();

		updatePurchaseOrderStatus(po);

		auditService.log(AuditLogRequest.builder().actionCode("CREATE").moduleCode("PURCHASING")
				.entityType("GOODS_RECEIPT").entityId(receipt.getId()).description("Goods receipt recorded.")
				.afterData(snapshot(receipt)).build());

		return receipt;
	}

	private String normalize(String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}

	private UserEntity getCurrentUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {

			return null;
		}

		return userRepository.findByUsernameIgnoreCase(authentication.getName()).orElse(null);
	}

	private void createReceiptItem(GoodsReceiptEntity receipt, PurchaseOrderEntity po,
			CreateGoodsReceiptItemRequest request, UserEntity currentUser) {

		PurchaseOrderItemEntity poItem = poItemRepository
				.findByIdAndPurchaseOrder_Id(request.getPurchaseOrderItemId(), po.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Purchase order item not found."));

		BigDecimal alreadyReceived = goodsReceiptItemRepository.calculateReceivedQuantity(poItem.getId());

		BigDecimal newTotal = alreadyReceived.add(request.getReceivedQuantity());

		if (newTotal.compareTo(poItem.getOrderedQuantity()) > 0) {

			throw new BusinessRuleException("Received quantity exceeds the remaining quantity for ingredient "
					+ poItem.getIngredient().getCode() + ".");
		}

		BigDecimal receivedBaseQuantity = request.getReceivedQuantity().multiply(poItem.getBaseQuantityPerUnit())
				.setScale(4, RoundingMode.HALF_UP);

		BigDecimal baseUnitCost = poItem.getUnitPrice().divide(poItem.getBaseQuantityPerUnit(), 6,
				RoundingMode.HALF_UP);

		GoodsReceiptItemEntity item = new GoodsReceiptItemEntity();

		item.setGoodsReceipt(receipt);
		item.setPurchaseOrderItem(poItem);

		item.setReceivedQuantity(request.getReceivedQuantity());

		item.setBaseQuantityPerUnit(poItem.getBaseQuantityPerUnit());

		item.setUnitPrice(poItem.getUnitPrice());

		item.setReceivedBaseQuantity(receivedBaseQuantity);

		item.setBaseUnitCost(baseUnitCost);

		item.setNotes(normalize(request.getNotes()));

		item = goodsReceiptItemRepository.saveAndFlush(item);

		inventoryPostingService.recordPurchaseReceipt(poItem.getIngredient().getId(), receivedBaseQuantity,
				baseUnitCost, receipt.getReceivedAt(), item.getId(), receipt.getReceiptNumber(), currentUser);
	}

	private void updatePurchaseOrderStatus(PurchaseOrderEntity po) {

		List<PurchaseOrderItemEntity> items = poItemRepository.findByPurchaseOrder_IdOrderByIdAsc(po.getId());

		boolean anythingReceived = false;
		boolean everythingReceived = true;

		for (PurchaseOrderItemEntity item : items) {

			BigDecimal received = goodsReceiptItemRepository.calculateReceivedQuantity(item.getId());

			if (received.compareTo(BigDecimal.ZERO) > 0) {

				anythingReceived = true;
			}

			if (received.compareTo(item.getOrderedQuantity()) < 0) {

				everythingReceived = false;
			}
		}

		if (everythingReceived) {

			po.setStatus(getStatus("RECEIVED"));

		} else if (anythingReceived) {

			po.setStatus(getStatus("PARTIALLY_RECEIVED"));
		}

		purchaseOrderRepository.save(po);
	}

	private PurchaseOrderStatusEntity getStatus(String code) {

		return statusRepository.findByCodeIgnoreCase(code)
				.orElseThrow(() -> new ResourceNotFoundException("Purchase order status not found: " + code));
	}

	private Map<String, Object> snapshot(GoodsReceiptEntity receipt) {

		Map<String, Object> data = new LinkedHashMap<>();

		data.put("receiptNumber", receipt.getReceiptNumber());

		data.put("purchaseOrder", receipt.getPurchaseOrder().getPoNumber());

		data.put("receivedAt", receipt.getReceivedAt());

		data.put("supplierDeliveryNo", receipt.getSupplierDeliveryNo());

		return data;
	}

	public GoodsReceiptResponse receiveResponse(Long purchaseOrderId, CreateGoodsReceiptRequest request) {

		GoodsReceiptEntity receipt = receive(purchaseOrderId, request);

		return buildResponse(receipt);
	}

	private GoodsReceiptResponse buildResponse(GoodsReceiptEntity receipt) {

		List<GoodsReceiptItemResponse> items = goodsReceiptItemRepository
				.findByGoodsReceipt_IdOrderByIdAsc(receipt.getId()).stream()
				.map(procurementMapper::toGoodsReceiptItemResponse).toList();

		return procurementMapper.toGoodsReceiptResponse(receipt, items);
	}

}
