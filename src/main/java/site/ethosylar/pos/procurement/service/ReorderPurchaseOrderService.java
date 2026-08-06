package site.ethosylar.pos.procurement.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.procurement.request.CreatePurchaseOrderItemRequest;
import site.ethosylar.pos.procurement.request.CreatePurchaseOrderRequest;
import site.ethosylar.pos.procurement.request.GenerateReorderPurchaseOrdersRequest;
import site.ethosylar.pos.procurement.response.PurchaseOrderResponse;
import site.ethosylar.pos.procurement.response.ReorderRecommendationResponse;

@Service
@RequiredArgsConstructor
@Transactional
public class ReorderPurchaseOrderService {

	private final ReorderRecommendationService recommendationService;

	private final PurchaseOrderService purchaseOrderService;

	public List<PurchaseOrderResponse> generateDraftPurchaseOrders(GenerateReorderPurchaseOrdersRequest request) {

		List<ReorderRecommendationResponse> recommendations = recommendationService
				.getRecommendationsForIngredients(request.getIngredientIds());

		if (recommendations.isEmpty()) {

			throw new BusinessRuleException("No reorder recommendations were found for the selected ingredients.");
		}

		validateSelection(request.getIngredientIds(), recommendations);

		/*
		 * Supplier ID -> recommendations
		 */
		Map<Long, List<ReorderRecommendationResponse>> bySupplier = new LinkedHashMap<>();

		for (ReorderRecommendationResponse recommendation : recommendations) {

			if (!Boolean.TRUE.equals(recommendation.getActionable())) {

				throw new BusinessRuleException("Ingredient " + recommendation.getIngredientCode()
						+ " cannot be converted into a purchase order: " + recommendation.getMessage());
			}

			if (recommendation.getPreferredSupplierId() == null) {

				throw new BusinessRuleException(
						"Preferred supplier is missing for ingredient " + recommendation.getIngredientCode() + ".");
			}

			bySupplier.computeIfAbsent(recommendation.getPreferredSupplierId(), key -> new ArrayList<>())
					.add(recommendation);
		}

		List<PurchaseOrderResponse> created = new ArrayList<>();

		for (Map.Entry<Long, List<ReorderRecommendationResponse>> entry : bySupplier.entrySet()) {

			CreatePurchaseOrderRequest poRequest = buildPurchaseOrderRequest(entry.getKey(), entry.getValue(), request);

			PurchaseOrderResponse po = purchaseOrderService.createResponse(poRequest);

			created.add(po);
		}

		return created;
	}

	private void validateSelection(List<Long> selectedIngredientIds,
			List<ReorderRecommendationResponse> recommendations) {

		Map<Long, ReorderRecommendationResponse> found = new LinkedHashMap<>();

		for (ReorderRecommendationResponse recommendation : recommendations) {

			found.put(recommendation.getIngredientId(), recommendation);
		}

		for (Long ingredientId : selectedIngredientIds) {

			if (!found.containsKey(ingredientId)) {

				throw new BusinessRuleException(
						"Ingredient " + ingredientId + " no longer requires reorder or is not eligible for reorder.");
			}
		}
	}

	private CreatePurchaseOrderRequest buildPurchaseOrderRequest(Long supplierId,

			List<ReorderRecommendationResponse> recommendations,

			GenerateReorderPurchaseOrdersRequest generationRequest) {

		CreatePurchaseOrderRequest request = new CreatePurchaseOrderRequest();

		request.setSupplierId(supplierId);

		request.setOrderDate(LocalDate.now());

		request.setExpectedDeliveryDate(generationRequest.getExpectedDeliveryDate());

		/*
		 * User can edit these on the resulting DRAFT PO later.
		 */
		request.setTaxAmount(BigDecimal.ZERO);

		request.setDeliveryFee(BigDecimal.ZERO);

		request.setNotes(buildNotes(generationRequest.getNotes()));

		List<CreatePurchaseOrderItemRequest> items = new ArrayList<>();

		for (ReorderRecommendationResponse recommendation : recommendations) {

			CreatePurchaseOrderItemRequest item = new CreatePurchaseOrderItemRequest();

			item.setIngredientId(recommendation.getIngredientId());

			/*
			 * IMPORTANT:
			 *
			 * orderedQuantity is purchase units / packs.
			 *
			 * Example: shortage = 3200g pack = 1000g suggestedPackCount = 4
			 *
			 * orderedQuantity = 4
			 */
			item.setOrderedQuantity(recommendation.getSuggestedPackCount());

			/*
			 * Snapshot the currently recommended supplier price.
			 *
			 * PurchaseOrderService also supports null here and would fall back to supplier
			 * master price, but explicitly passing it makes the generated recommendation
			 * easier to reason about.
			 */
			item.setUnitPrice(recommendation.getPurchasePrice());

			item.setNotes(String.format(
					"Auto-generated from reorder recommendation. " + "Current stock: %s, target: %s, "
							+ "suggested base quantity: %s.",
					recommendation.getCurrentStock().toPlainString(),
					recommendation.getTargetStockLevel().toPlainString(),
					recommendation.getSuggestedPurchaseQuantity().toPlainString()));

			items.add(item);
		}

		request.setItems(items);

		return request;
	}

	private String buildNotes(String userNotes) {

		String generated = "Draft purchase order generated from inventory reorder recommendations.";

		if (userNotes == null || userNotes.isBlank()) {

			return generated;
		}

		return generated + " " + userNotes.trim();
	}

}