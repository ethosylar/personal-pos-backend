package site.ethosylar.pos.sale.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.accounting.service.AccountingPeriodService;
import site.ethosylar.pos.audit.request.AuditLogRequest;
import site.ethosylar.pos.audit.service.AuditService;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.common.outbox.OutboxAggregateTypes;
import site.ethosylar.pos.common.outbox.OutboxEventTypes;
import site.ethosylar.pos.common.outbox.service.OutboxService;
import site.ethosylar.pos.ingredient.entity.IngredientEntity;
import site.ethosylar.pos.ingredient.repository.IngredientRepository;
import site.ethosylar.pos.inventory.repository.InventoryMovementRepository;
import site.ethosylar.pos.inventory.service.InventoryPostingService;
import site.ethosylar.pos.payment.repository.PaymentRepository;
import site.ethosylar.pos.payment.request.CheckoutPaymentRequest;
import site.ethosylar.pos.payment.response.PaymentResponse;
import site.ethosylar.pos.payment.service.PaymentService;
import site.ethosylar.pos.product.entity.ProductEntity;
import site.ethosylar.pos.recipe.entity.RecipeEntity;
import site.ethosylar.pos.recipe.entity.RecipeItemEntity;
import site.ethosylar.pos.recipe.model.RecipeCostingResult;
import site.ethosylar.pos.recipe.model.RecipeItemCostResult;
import site.ethosylar.pos.recipe.repository.RecipeItemRepository;
import site.ethosylar.pos.recipe.repository.RecipeRepository;
import site.ethosylar.pos.recipe.service.RecipeCostingService;
import site.ethosylar.pos.sale.entity.SaleEntity;
import site.ethosylar.pos.sale.entity.SaleItemEntity;
import site.ethosylar.pos.sale.entity.SaleStatusEntity;
import site.ethosylar.pos.sale.event.SaleCompletedEventPayload;
import site.ethosylar.pos.sale.mapper.SaleMapper;
import site.ethosylar.pos.sale.model.SaleIngredientUsage;
import site.ethosylar.pos.sale.repository.SaleItemRepository;
import site.ethosylar.pos.sale.repository.SaleRepository;
import site.ethosylar.pos.sale.repository.SaleStatusRepository;
import site.ethosylar.pos.sale.request.CompleteSaleRequest;
import site.ethosylar.pos.sale.response.SaleItemResponse;
import site.ethosylar.pos.sale.response.SaleResponse;
import site.ethosylar.pos.user.entity.UserEntity;
import site.ethosylar.pos.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleCheckoutService {

	private final SaleRepository saleRepository;
	private final SaleItemRepository saleItemRepository;
	private final SaleStatusRepository saleStatusRepository;

	private final RecipeRepository recipeRepository;
	private final RecipeItemRepository recipeItemRepository;

	private final RecipeCostingService recipeCostingService;

	private final IngredientRepository ingredientRepository;

	private final InventoryMovementRepository inventoryMovementRepository;

	private final InventoryPostingService inventoryPostingService;

	private final PaymentService paymentService;
	private final PaymentRepository paymentRepository;

	private final UserRepository userRepository;

	private final SaleMapper saleMapper;

	private final AuditService auditService;

	private final AccountingPeriodService accountingPeriodService;

	private final OutboxService outboxService;

	@Transactional
	public SaleResponse complete(Long saleId, CompleteSaleRequest request) {

		SaleEntity sale = saleRepository.findByIdForUpdate(saleId)
				.orElseThrow(() -> new ResourceNotFoundException("Sale not found."));

		if (!"DRAFT".equalsIgnoreCase(sale.getStatus().getCode())) {

			throw new BusinessRuleException("Only draft sales can be completed.");
		}

		List<SaleItemEntity> saleItems = saleItemRepository.findBySale_IdOrderByIdAsc(sale.getId());

		if (saleItems.isEmpty()) {

			throw new BusinessRuleException("Sale must contain at least one item.");
		}

		validatePaymentTotal(sale, request);

		/*
		 * Ingredient ID -> quantity required.
		 *
		 * We aggregate BEFORE locking so beans used in multiple products are checked
		 * together.
		 */
		Map<Long, BigDecimal> ingredientRequirements = new LinkedHashMap<>();

		Map<Long, BigDecimal> ingredientUnitCosts = new LinkedHashMap<>();

		Map<Long, List<SaleIngredientUsage>> usageBySaleItem = new LinkedHashMap<>();

		BigDecimal totalCost = BigDecimal.ZERO;

		for (SaleItemEntity saleItem : saleItems) {

			ProductEntity product = saleItem.getProduct();

			if (!Boolean.TRUE.equals(product.getActive()) || !Boolean.TRUE.equals(product.getAvailable())) {

				throw new BusinessRuleException("Product " + product.getCode() + " is unavailable.");
			}

			RecipeEntity recipe = recipeRepository.findByProduct_IdAndCurrentTrue(product.getId())
					.orElseThrow(() -> new BusinessRuleException(
							"No current recipe is configured for product " + product.getCode() + "."));

			List<RecipeItemEntity> recipeItems = recipeItemRepository
					.findByRecipe_IdOrderByDisplayOrderAscIdAsc(recipe.getId());

			RecipeCostingResult costing = recipeCostingService.calculate(recipe, recipeItems);

			if (!Boolean.TRUE.equals(costing.getAllCostsAvailable())) {

				throw new BusinessRuleException("Costing is incomplete for product " + product.getCode() + ".");
			}

			BigDecimal unitCost = costing.getCostPerPortion();

			BigDecimal lineCost = unitCost.multiply(saleItem.getQuantity()).setScale(6, RoundingMode.HALF_UP);

			saleItem.setRecipe(recipe);

			saleItem.setRecipeVersionNo(recipe.getVersionNo());

			saleItem.setUnitCostSnapshot(unitCost);

			saleItem.setLineCostSnapshot(lineCost);

			saleItem.setGrossProfitSnapshot(
					saleItem.getLineTotal().subtract(lineCost).setScale(6, RoundingMode.HALF_UP));

			totalCost = totalCost.add(lineCost);

			List<SaleIngredientUsage> usages = new ArrayList<>();

			for (RecipeItemEntity recipeItem : recipeItems) {

				RecipeItemCostResult itemCost = findCostResult(costing, recipeItem.getId());

				/*
				 * Recipe quantities represent the whole recipe batch.
				 *
				 * If recipe yields 10 portions, divide ingredient usage by 10 before
				 * multiplying by quantity sold.
				 */
				BigDecimal quantityPerPortion = itemCost.getEffectiveQuantity().divide(recipe.getYieldQuantity(), 6,
						RoundingMode.HALF_UP);

				BigDecimal requiredQuantity = quantityPerPortion.multiply(saleItem.getQuantity()).setScale(6,
						RoundingMode.HALF_UP);

				Long ingredientId = recipeItem.getIngredient().getId();

				ingredientRequirements.merge(ingredientId, requiredQuantity, BigDecimal::add);

				ingredientUnitCosts.put(ingredientId, itemCost.getUnitCost());

				usages.add(new SaleIngredientUsage(ingredientId, requiredQuantity, itemCost.getUnitCost()));
			}

			usageBySaleItem.put(saleItem.getId(), usages);
		}

		List<Long> ingredientIds = ingredientRequirements.keySet().stream().sorted().toList();

		for (Long ingredientId : ingredientIds) {

			IngredientEntity ingredient = ingredientRepository.findByIdForUpdate(ingredientId)
					.orElseThrow(() -> new ResourceNotFoundException("Ingredient not found."));

			BigDecimal stock = inventoryMovementRepository.calculateCurrentStock(ingredientId);

			BigDecimal required = ingredientRequirements.get(ingredientId);

			if (stock.compareTo(required) < 0) {

				throw new BusinessRuleException("Insufficient stock for ingredient " + ingredient.getCode()
						+ ". Available: " + stock + ", required: " + required + ".");
			}
		}

		OffsetDateTime completedAt = OffsetDateTime.now();

		accountingPeriodService.ensureOpen(completedAt);

		UserEntity cashier = getCurrentUser();

		saleItemRepository.saveAll(saleItems);

		for (SaleItemEntity saleItem : saleItems) {

			List<SaleIngredientUsage> usages = usageBySaleItem.get(saleItem.getId());

			for (SaleIngredientUsage usage : usages) {

				inventoryPostingService.recordSaleUsage(usage.ingredientId(), usage.quantity(), usage.unitCost(),
						completedAt, saleItem.getId(), sale.getSaleNumber(), cashier);
			}
		}

		sale.setTotalCostSnapshot(totalCost.setScale(6, RoundingMode.HALF_UP));

		BigDecimal netSalesBeforeTax = sale.getSubtotal().subtract(sale.getDiscountAmount());

		sale.setGrossProfitSnapshot(netSalesBeforeTax.subtract(totalCost).setScale(6, RoundingMode.HALF_UP));

		for (CheckoutPaymentRequest paymentRequest : request.getPayments()) {

			paymentService.createCompletedPayment(sale, paymentRequest, cashier, completedAt);
		}

		sale.setCashierUser(cashier);

		sale.setSoldAt(completedAt);

		sale.setStatus(getSaleStatus("COMPLETED"));

		saleRepository.save(sale);

		SaleCompletedEventPayload payload = new SaleCompletedEventPayload(sale.getId(), sale.getSaleNumber(),
				sale.getSoldAt(), sale.getSubtotal(), sale.getTotalAmount(), sale.getTotalCostSnapshot(),
				sale.getGrossProfitSnapshot());

		outboxService.record(OutboxAggregateTypes.SALE, sale.getId(), OutboxEventTypes.SALE_COMPLETED, payload);

		auditService.log(AuditLogRequest.builder().actionCode("STATUS_CHANGE").moduleCode("POS").entityType("SALE")
				.entityId(sale.getId()).description("POS sale completed.").afterData(snapshot(sale)).build());

		return buildResponse(sale);

	}

	private RecipeItemCostResult findCostResult(RecipeCostingResult costing, Long itemId) {

		return costing.getItems().stream().filter(item -> itemId.equals(item.getRecipeItemId())).findFirst()
				.orElseThrow(() -> new BusinessRuleException("Recipe item costing result not found."));
	}

	private UserEntity getCurrentUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {

			return null;
		}

		return userRepository.findByUsernameIgnoreCase(authentication.getName()).orElse(null);
	}

	private SaleStatusEntity getSaleStatus(String code) {

		return saleStatusRepository.findByCodeIgnoreCase(code)
				.orElseThrow(() -> new ResourceNotFoundException("Sale status not found: " + code));
	}

	private void validatePaymentTotal(SaleEntity sale, CompleteSaleRequest request) {

		BigDecimal paymentTotal = request.getPayments().stream().map(CheckoutPaymentRequest::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add).setScale(4, RoundingMode.HALF_UP);

		BigDecimal saleTotal = sale.getTotalAmount().setScale(4, RoundingMode.HALF_UP);

		if (paymentTotal.compareTo(saleTotal) != 0) {

			throw new BusinessRuleException("Payment total must equal sale total. " + "Sale total: " + saleTotal
					+ ", payment total: " + paymentTotal + ".");
		}
	}

	private SaleResponse buildResponse(SaleEntity sale) {

		List<SaleItemResponse> items = saleItemRepository.findBySale_IdOrderByIdAsc(sale.getId()).stream()
				.map(saleMapper::toItemResponse).toList();

		List<PaymentResponse> payments = paymentRepository.findBySale_IdOrderByCreatedAtAsc(sale.getId()).stream()
				.map(saleMapper::toPaymentResponse).toList();

		return saleMapper.toResponse(sale, items, payments);
	}

	private Map<String, Object> snapshot(SaleEntity sale) {

		Map<String, Object> data = new LinkedHashMap<>();

		data.put("saleNumber", sale.getSaleNumber());

		data.put("status", sale.getStatus() != null ? sale.getStatus().getCode() : null);

		data.put("cashierUserId", sale.getCashierUser() != null ? sale.getCashierUser().getId() : null);

		data.put("cashierUsername", sale.getCashierUser() != null ? sale.getCashierUser().getUsername() : null);

		data.put("soldAt", sale.getSoldAt());

		data.put("subtotal", sale.getSubtotal());

		data.put("discountAmount", sale.getDiscountAmount());

		data.put("taxAmount", sale.getTaxAmount());

		data.put("totalAmount", sale.getTotalAmount());

		data.put("totalCostSnapshot", sale.getTotalCostSnapshot());

		data.put("grossProfitSnapshot", sale.getGrossProfitSnapshot());

		return data;
	}
}
