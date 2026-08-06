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
	
	public List<PurchaseOrderResponse>
    generateDraftPurchaseOrders(
            GenerateReorderPurchaseOrdersRequest request) {

List<ReorderRecommendationResponse>
        recommendations =
            recommendationService
                .getRecommendationsForIngredients(
                    request.getIngredientIds()
                );

if (recommendations.isEmpty()) {

    throw new BusinessRuleException(
        "No reorder recommendations were found for the selected ingredients."
    );
}

validateSelection(
    request.getIngredientIds(),
    recommendations
);

/*
 * Supplier ID -> recommendations
 */
Map<Long, List<ReorderRecommendationResponse>>
        bySupplier =
            new LinkedHashMap<>();

for (ReorderRecommendationResponse recommendation
        : recommendations) {

    if (!Boolean.TRUE.equals(
            recommendation.getActionable())) {

        throw new BusinessRuleException(
            "Ingredient "
            + recommendation.getIngredientCode()
            + " cannot be converted into a purchase order: "
            + recommendation.getMessage()
        );
    }

    if (recommendation
            .getPreferredSupplierId() == null) {

        throw new BusinessRuleException(
            "Preferred supplier is missing for ingredient "
            + recommendation.getIngredientCode()
            + "."
        );
    }

    bySupplier
        .computeIfAbsent(
            recommendation
                .getPreferredSupplierId(),
            key -> new ArrayList<>()
        )
        .add(recommendation);
}

List<PurchaseOrderResponse> created =
        new ArrayList<>();

for (Map.Entry<
        Long,
        List<ReorderRecommendationResponse>>
        entry : bySupplier.entrySet()) {

    CreatePurchaseOrderRequest poRequest =
            buildPurchaseOrderRequest(
                entry.getKey(),
                entry.getValue(),
                request
            );

    PurchaseOrderResponse po =
            purchaseOrderService
                .createResponse(poRequest);

    created.add(po);
}

return created;
}
	
	private void validateSelection(
	        List<Long> selectedIngredientIds,
	        List<ReorderRecommendationResponse>
	                recommendations) {

	    Map<Long, ReorderRecommendationResponse>
	            found = new LinkedHashMap<>();

	    for (ReorderRecommendationResponse recommendation
	            : recommendations) {

	        found.put(
	            recommendation.getIngredientId(),
	            recommendation
	        );
	    }

	    for (Long ingredientId
	            : selectedIngredientIds) {

	        if (!found.containsKey(
	                ingredientId)) {

	            throw new BusinessRuleException(
	                "Ingredient "
	                + ingredientId
	                + " no longer requires reorder or is not eligible for reorder."
	            );
	        }
	    }
	}
	
	

}