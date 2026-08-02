package site.ethosylar.pos.procurement.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.request.AuditLogRequest;
import site.ethosylar.pos.audit.service.AuditService;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.DuplicateResourceException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.ingredient.entity.IngredientEntity;
import site.ethosylar.pos.ingredient.entity.IngredientSupplierEntity;
import site.ethosylar.pos.ingredient.repository.IngredientRepository;
import site.ethosylar.pos.ingredient.repository.IngredientSupplierRepository;
import site.ethosylar.pos.procurement.entity.PurchaseOrderEntity;
import site.ethosylar.pos.procurement.entity.PurchaseOrderItemEntity;
import site.ethosylar.pos.procurement.entity.PurchaseOrderStatusEntity;
import site.ethosylar.pos.procurement.mapper.ProcurementMapper;
import site.ethosylar.pos.procurement.repository.GoodsReceiptItemRepository;
import site.ethosylar.pos.procurement.repository.GoodsReceiptRepository;
import site.ethosylar.pos.procurement.repository.PurchaseOrderItemRepository;
import site.ethosylar.pos.procurement.repository.PurchaseOrderRepository;
import site.ethosylar.pos.procurement.repository.PurchaseOrderStatusRepository;
import site.ethosylar.pos.procurement.request.CreatePurchaseOrderItemRequest;
import site.ethosylar.pos.procurement.request.CreatePurchaseOrderRequest;
import site.ethosylar.pos.procurement.response.PurchaseOrderItemResponse;
import site.ethosylar.pos.procurement.response.PurchaseOrderResponse;
import site.ethosylar.pos.supplier.entity.SupplierEntity;
import site.ethosylar.pos.supplier.repository.SupplierRepository;
import site.ethosylar.pos.user.entity.UserEntity;
import site.ethosylar.pos.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderService {

	private final PurchaseOrderRepository purchaseOrderRepository;
	private final PurchaseOrderItemRepository itemRepository;
	private final PurchaseOrderStatusRepository statusRepository;

	private final SupplierRepository supplierRepository;

	private final IngredientRepository ingredientRepository;
	private final IngredientSupplierRepository ingredientSupplierRepository;

	private final ProcurementNumberService numberService;

	private final UserRepository userRepository;

	private final AuditService auditService;

	private final GoodsReceiptRepository goodsReceiptRepository;

	private final ProcurementMapper procurementMapper;

	private final GoodsReceiptItemRepository goodsReceiptItemRepository;

	public PurchaseOrderEntity create(CreatePurchaseOrderRequest request) {

		SupplierEntity supplier = supplierRepository.findById(request.getSupplierId())
				.orElseThrow(() -> new ResourceNotFoundException("Supplier not found."));

		if (!Boolean.TRUE.equals(supplier.getActive())) {

			throw new BusinessRuleException("Supplier is inactive.");
		}

		validateDates(request.getOrderDate(), request.getExpectedDeliveryDate());

		PurchaseOrderStatusEntity draft = getStatus("DRAFT");

		PurchaseOrderEntity po = new PurchaseOrderEntity();

		po.setPoNumber(numberService.nextPurchaseOrderNumber());

		po.setSupplier(supplier);
		po.setStatus(draft);

		po.setOrderDate(request.getOrderDate());

		po.setExpectedDeliveryDate(request.getExpectedDeliveryDate());

		po.setTaxAmount(request.getTaxAmount());
		po.setDeliveryFee(request.getDeliveryFee());

		po.setNotes(normalize(request.getNotes()));

		po.setCreatedByUser(getCurrentUser());

		po.setSubtotal(BigDecimal.ZERO);

		po.setTotalAmount(request.getTaxAmount().add(request.getDeliveryFee()));

		po = purchaseOrderRepository.save(po);

		for (CreatePurchaseOrderItemRequest itemRequest : request.getItems()) {

			addItemInternal(po, itemRequest);
		}

		recalculateTotals(po);

		auditService.log(
				AuditLogRequest.builder().actionCode("CREATE").moduleCode("PURCHASING").entityType("PURCHASE_ORDER")
						.entityId(po.getId()).description("Purchase order created.").afterData(snapshot(po)).build());

		return po;
	}

	private void recalculateTotals(PurchaseOrderEntity po) {

		List<PurchaseOrderItemEntity> items = itemRepository.findByPurchaseOrder_IdOrderByIdAsc(po.getId());

		BigDecimal subtotal = items.stream().map(PurchaseOrderItemEntity::getLineSubtotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add).setScale(4, RoundingMode.HALF_UP);

		po.setSubtotal(subtotal);

		po.setTotalAmount(subtotal.add(po.getTaxAmount()).add(po.getDeliveryFee()).setScale(4, RoundingMode.HALF_UP));

		purchaseOrderRepository.save(po);
	}

	public PurchaseOrderEntity placeOrder(Long id) {

		PurchaseOrderEntity po = getForUpdate(id);

		ensureDraft(po);

		List<PurchaseOrderItemEntity> items = itemRepository.findByPurchaseOrder_IdOrderByIdAsc(po.getId());

		if (items.isEmpty()) {

			throw new BusinessRuleException("Purchase order must contain at least one item.");
		}

		Map<String, Object> before = snapshot(po);

		recalculateTotals(po);

		po.setStatus(getStatus("ORDERED"));

		purchaseOrderRepository.save(po);

		auditService.log(AuditLogRequest.builder().actionCode("STATUS_CHANGE").moduleCode("PURCHASING")
				.entityType("PURCHASE_ORDER").entityId(po.getId()).description("Purchase order placed.")
				.beforeData(before).afterData(snapshot(po)).build());

		return po;
	}

	public PurchaseOrderEntity cancel(Long id) {

		PurchaseOrderEntity po = getForUpdate(id);

		String status = po.getStatus().getCode();

		if (!status.equals("DRAFT") && !status.equals("ORDERED")) {

			throw new BusinessRuleException("Purchase order cannot be cancelled in its current status.");
		}

		if (goodsReceiptRepository.existsByPurchaseOrder_Id(po.getId())) {

			throw new BusinessRuleException("Purchase order cannot be cancelled after goods have been received.");
		}

		Map<String, Object> before = snapshot(po);

		po.setStatus(getStatus("CANCELLED"));

		purchaseOrderRepository.save(po);

		auditService.log(AuditLogRequest.builder().actionCode("STATUS_CHANGE").moduleCode("PURCHASING")
				.entityType("PURCHASE_ORDER").entityId(po.getId()).description("Purchase order cancelled.")
				.beforeData(before).afterData(snapshot(po)).build());

		return po;
	}

	private PurchaseOrderItemEntity addItemInternal(PurchaseOrderEntity po, CreatePurchaseOrderItemRequest request) {

		ensureDraft(po);

		IngredientEntity ingredient = ingredientRepository.findById(request.getIngredientId())
				.orElseThrow(() -> new ResourceNotFoundException("Ingredient not found."));

		if (!Boolean.TRUE.equals(ingredient.getActive())) {

			throw new BusinessRuleException("Ingredient is inactive.");
		}

		if (itemRepository.existsByPurchaseOrder_IdAndIngredient_Id(po.getId(), ingredient.getId())) {

			throw new DuplicateResourceException("Ingredient is already included in this purchase order.");
		}

		IngredientSupplierEntity supplierOption = ingredientSupplierRepository
				.findByIngredient_IdAndSupplier_Id(ingredient.getId(), po.getSupplier().getId())
				.orElseThrow(() -> new BusinessRuleException(
						"This ingredient is not configured for the selected supplier."));

		if (!Boolean.TRUE.equals(supplierOption.getActive())) {

			throw new BusinessRuleException("Ingredient supplier option is inactive.");
		}

		BigDecimal unitPrice = request.getUnitPrice() != null ? request.getUnitPrice()
				: supplierOption.getPurchasePrice();

		BigDecimal lineSubtotal = request.getOrderedQuantity().multiply(unitPrice).setScale(4, RoundingMode.HALF_UP);

		PurchaseOrderItemEntity item = new PurchaseOrderItemEntity();

		item.setPurchaseOrder(po);
		item.setIngredient(ingredient);

		item.setPurchaseUnit(supplierOption.getPurchaseUnit());

		item.setOrderedQuantity(request.getOrderedQuantity());

		item.setBaseQuantityPerUnit(supplierOption.getBaseQuantity());

		item.setUnitPrice(unitPrice);

		item.setLineSubtotal(lineSubtotal);

		item.setSupplierItemCode(supplierOption.getSupplierItemCode());

		item.setNotes(normalize(request.getNotes()));

		return itemRepository.save(item);
	}

	public PurchaseOrderResponse createResponse(CreatePurchaseOrderRequest request) {

		PurchaseOrderEntity po = create(request);

		return buildResponse(po);
	}

	public PurchaseOrderResponse placeOrderResponse(Long id) {

		PurchaseOrderEntity po = placeOrder(id);

		return buildResponse(po);
	}

	public PurchaseOrderResponse cancelResponse(Long id) {

		PurchaseOrderEntity po = cancel(id);

		return buildResponse(po);
	}

	private PurchaseOrderEntity getForUpdate(Long id) {

		return purchaseOrderRepository.findByIdForUpdate(id)
				.orElseThrow(() -> new ResourceNotFoundException("Purchase order not found."));
	}

	private PurchaseOrderStatusEntity getStatus(String code) {

		return statusRepository.findByCodeIgnoreCase(code)
				.orElseThrow(() -> new ResourceNotFoundException("Purchase order status not found: " + code));
	}

	private void ensureDraft(PurchaseOrderEntity po) {

		if (!"DRAFT".equalsIgnoreCase(po.getStatus().getCode())) {

			throw new BusinessRuleException("Only draft purchase orders can be modified.");
		}
	}

	private void validateDates(LocalDate orderDate, LocalDate expectedDeliveryDate) {

		if (expectedDeliveryDate != null && expectedDeliveryDate.isBefore(orderDate)) {

			throw new BusinessRuleException("Expected delivery date cannot be before order date.");
		}
	}

	private UserEntity getCurrentUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {

			return null;
		}

		return userRepository.findByUsernameIgnoreCase(authentication.getName()).orElse(null);
	}

	private String normalize(String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}

	private Map<String, Object> snapshot(PurchaseOrderEntity po) {

		Map<String, Object> data = new LinkedHashMap<>();

		data.put("poNumber", po.getPoNumber());

		data.put("supplier", po.getSupplier().getCode());

		data.put("status", po.getStatus().getCode());

		data.put("orderDate", po.getOrderDate());

		data.put("expectedDeliveryDate", po.getExpectedDeliveryDate());

		data.put("subtotal", po.getSubtotal());

		data.put("taxAmount", po.getTaxAmount());

		data.put("deliveryFee", po.getDeliveryFee());

		data.put("totalAmount", po.getTotalAmount());

		return data;
	}

	private PurchaseOrderResponse buildResponse(PurchaseOrderEntity po) {

		List<PurchaseOrderItemResponse> items = itemRepository.findByPurchaseOrder_IdOrderByIdAsc(po.getId()).stream()
				.map(this::buildItemResponse).toList();

		return ProcurementMapper.toPurchaseOrderResponse(po, items);
	}

	private PurchaseOrderItemResponse buildItemResponse(PurchaseOrderItemEntity item) {

		BigDecimal received = goodsReceiptItemRepository.calculateReceivedQuantity(item.getId());

		BigDecimal remaining = item.getOrderedQuantity().subtract(received);

		return procurementMapper.toPurchaseOrderItemResponse(item, received, remaining);
	}

}
