package site.ethosylar.pos.procurement.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.ingredient.repository.IngredientSupplierRepository;
import site.ethosylar.pos.inventory.projection.InventoryStockProjection;
import site.ethosylar.pos.inventory.repository.InventoryMovementRepository;
import site.ethosylar.pos.procurement.response.ReorderRecommendationResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReorderRecommendationService {

	private final InventoryMovementRepository inventoryMovementRepository;

	/*
	 * Inject the SAME ingredient supplier repository used by IngredientCostService.
	 */
	private final IngredientSupplierRepository ingredientSupplierRepository;

	public List<ReorderRecommendationResponse> getRecommendations() {

		return inventoryMovementRepository.findInventoryStockIntelligence().stream()

				.filter(this::requiresReorder)

				.map(this::buildRecommendation)

				/*
				 * Out of stock / lowest stock will naturally be useful near the top.
				 */
				.sorted((a, b) -> a.getCurrentStock().compareTo(b.getCurrentStock()))

				.toList();
	}

	private boolean requiresReorder(InventoryStockProjection stock) {

		if (!Boolean.TRUE.equals(stock.getAlertEnabled())) {

			return false;
		}

		BigDecimal current = zero(stock.getCurrentStock());

		BigDecimal reorder = zero(stock.getReorderLevel());

		return current.compareTo(reorder) <= 0;
	}

	private ReorderRecommendationResponse buildRecommendation(InventoryStockProjection stock) {

		BigDecimal current = zero(stock.getCurrentStock());

		BigDecimal target = stock.getTargetStockLevel();

		String status = current.compareTo(BigDecimal.ZERO) <= 0 ? "OUT_OF_STOCK" : "LOW_STOCK";

		if (target == null) {

			return ReorderRecommendationResponse.builder()

					.ingredientId(stock.getIngredientId())

					.ingredientCode(stock.getIngredientCode())

					.ingredientName(stock.getIngredientName())

					.currentStock(current)

					.reorderLevel(zero(stock.getReorderLevel()))

					.targetStockLevel(null)

					.stockStatus(status)

					.actionable(false)

					.message("Target stock level is not configured.")

					.build();
		}

		BigDecimal shortage = target.subtract(current);

		if (shortage.compareTo(BigDecimal.ZERO) < 0) {

			shortage = BigDecimal.ZERO;
		}

		shortage = shortage.setScale(4, RoundingMode.HALF_UP);

		var preferredSupplier = ingredientSupplierRepository
				.findFirstByIngredient_IdAndPreferredTrueAndActiveTrue(stock.getIngredientId()).orElse(null);

		if (preferredSupplier == null) {

			return ReorderRecommendationResponse.builder()

					.ingredientId(stock.getIngredientId())

					.ingredientCode(stock.getIngredientCode())

					.ingredientName(stock.getIngredientName())

					.currentStock(current)

					.reorderLevel(zero(stock.getReorderLevel()))

					.targetStockLevel(target)

					.stockStatus(status)

					.shortageQuantity(shortage)

					.actionable(false)

					.message("Preferred supplier is not configured.")

					.build();
		}

		BigDecimal supplierBaseQuantity = preferredSupplier.getBaseQuantity();

		BigDecimal purchasePrice = preferredSupplier.getPurchasePrice();

		if (supplierBaseQuantity == null || supplierBaseQuantity.compareTo(BigDecimal.ZERO) <= 0
				|| purchasePrice == null) {

			return ReorderRecommendationResponse.builder()

					.ingredientId(stock.getIngredientId())

					.ingredientCode(stock.getIngredientCode())

					.ingredientName(stock.getIngredientName())

					.currentStock(current)

					.reorderLevel(zero(stock.getReorderLevel()))

					.targetStockLevel(target)

					.stockStatus(status)

					.shortageQuantity(shortage)

					.actionable(false)

					.message("Preferred supplier purchasing configuration is incomplete.")

					.build();
		}

		BigDecimal packCount = shortage.divide(supplierBaseQuantity, 0, RoundingMode.CEILING);

		BigDecimal purchaseQuantity = supplierBaseQuantity.multiply(packCount).setScale(4, RoundingMode.HALF_UP);

		BigDecimal estimatedCost = purchasePrice.multiply(packCount).setScale(4, RoundingMode.HALF_UP);

		return ReorderRecommendationResponse.builder()

				.ingredientId(stock.getIngredientId())

				.ingredientCode(stock.getIngredientCode())

				.ingredientName(stock.getIngredientName())

				.currentStock(current)

				.reorderLevel(zero(stock.getReorderLevel()))

				.targetStockLevel(target)

				.stockStatus(status)

				.shortageQuantity(shortage)

				.preferredSupplierId(preferredSupplier.getSupplier().getId())

				.preferredSupplierCode(preferredSupplier.getSupplier().getCode())

				.preferredSupplierName(preferredSupplier.getSupplier().getName())

				.supplierBaseQuantity(supplierBaseQuantity)

				.purchasePrice(purchasePrice)

				.suggestedPackCount(packCount)

				.suggestedPurchaseQuantity(purchaseQuantity)

				.estimatedCost(estimatedCost)

				.actionable(true)

				.message(null)

				.build();

	}

	private BigDecimal zero(BigDecimal value) {

		return value != null ? value : BigDecimal.ZERO;
	}

	public List<ReorderRecommendationResponse> getRecommendationsForIngredients(List<Long> ingredientIds) {

		Set<Long> selected = new HashSet<>(ingredientIds);

		return getRecommendations().stream().filter(r -> selected.contains(r.getIngredientId())).toList();
	}

}