package site.ethosylar.pos.recipe.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Component;

import site.ethosylar.pos.recipe.entity.RecipeEntity;
import site.ethosylar.pos.recipe.entity.RecipeItemEntity;
import site.ethosylar.pos.recipe.model.RecipeCostingResult;
import site.ethosylar.pos.recipe.model.RecipeItemCostResult;
import site.ethosylar.pos.recipe.response.RecipeDetailResponse;
import site.ethosylar.pos.recipe.response.RecipeItemResponse;
import site.ethosylar.pos.recipe.response.RecipeSummaryResponse;

@Component
public class RecipeMapper {

	public RecipeSummaryResponse toSummaryResponse(RecipeEntity recipe) {

		return RecipeSummaryResponse.builder().id(recipe.getId())

				.productId(recipe.getProduct().getId()).productCode(recipe.getProduct().getCode())
				.productName(recipe.getProduct().getName())

				.versionNo(recipe.getVersionNo())

				.lifecycleStatus(resolveLifecycleStatus(recipe))

				.yieldQuantity(recipe.getYieldQuantity())

				.current(recipe.getCurrent())

				.activatedCostSnapshot(recipe.getActivatedCostSnapshot())

				.effectiveFrom(recipe.getEffectiveFrom()).effectiveTo(recipe.getEffectiveTo())
				.activatedAt(recipe.getActivatedAt())

				.createdAt(recipe.getCreatedAt()).updatedAt(recipe.getUpdatedAt())

				.build();
	}

	public RecipeDetailResponse toDetailResponse(RecipeEntity recipe, List<RecipeItemEntity> items,
			RecipeCostingResult costing) {

		List<RecipeItemResponse> itemResponses = items.stream()
				.map(item -> toItemResponse(item, findCostResult(costing, item.getId()))).toList();

		BigDecimal grossProfit = null;
		BigDecimal marginPercent = null;

		BigDecimal sellingPrice = recipe.getProduct().getSellingPrice();

		BigDecimal costPerPortion = costing.getCostPerPortion();

		if (costPerPortion != null) {

			grossProfit = sellingPrice.subtract(costPerPortion).setScale(6, RoundingMode.HALF_UP);

			if (sellingPrice.compareTo(BigDecimal.ZERO) > 0) {

				marginPercent = grossProfit.divide(sellingPrice, 8, RoundingMode.HALF_UP)
						.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);
			}
		}

		return RecipeDetailResponse.builder().id(recipe.getId())

				.productId(recipe.getProduct().getId()).productCode(recipe.getProduct().getCode())
				.productName(recipe.getProduct().getName()).productSellingPrice(sellingPrice)

				.versionNo(recipe.getVersionNo())

				.lifecycleStatus(resolveLifecycleStatus(recipe))

				.yieldQuantity(recipe.getYieldQuantity())

				.current(recipe.getCurrent())

				.currentBatchCost(costing.getBatchCost())

				.currentCostPerPortion(costPerPortion)

				.currentGrossProfit(grossProfit)

				.currentMarginPercent(marginPercent)

				.activatedCostSnapshot(recipe.getActivatedCostSnapshot())

				.allCostsAvailable(costing.getAllCostsAvailable())

				.costingWarnings(costing.getWarnings())

				.effectiveFrom(recipe.getEffectiveFrom())

				.effectiveTo(recipe.getEffectiveTo())

				.activatedAt(recipe.getActivatedAt())

				.notes(recipe.getNotes())

				.items(itemResponses)

				.createdAt(recipe.getCreatedAt()).updatedAt(recipe.getUpdatedAt())

				.build();
	}

	private RecipeItemResponse toItemResponse(RecipeItemEntity item, RecipeItemCostResult cost) {

		return RecipeItemResponse.builder().id(item.getId())

				.ingredientId(item.getIngredient().getId())

				.ingredientCode(item.getIngredient().getCode())

				.ingredientName(item.getIngredient().getName())

				.baseUnitId(item.getIngredient().getBaseUnit().getId())

				.baseUnitCode(item.getIngredient().getBaseUnit().getCode())

				.baseUnitSymbol(item.getIngredient().getBaseUnit().getSymbol())

				.quantity(item.getQuantity())

				.wastePercent(item.getWastePercent())

				.effectiveQuantity(cost.getEffectiveQuantity())

				.currentUnitCost(cost.getUnitCost())

				.currentLineCost(cost.getLineCost())

				.activatedUnitCostSnapshot(item.getUnitCostSnapshot())

				.activatedLineCostSnapshot(item.getLineCostSnapshot())

				.costAvailable(cost.getCostAvailable())

				.costingWarning(cost.getWarning())

				.displayOrder(item.getDisplayOrder())

				.notes(item.getNotes())

				.createdAt(item.getCreatedAt()).updatedAt(item.getUpdatedAt())

				.build();
	}

	private RecipeItemCostResult findCostResult(RecipeCostingResult costing, Long itemId) {

		return costing.getItems().stream().filter(item -> itemId.equals(item.getRecipeItemId())).findFirst()
				.orElseThrow();
	}

	private String resolveLifecycleStatus(RecipeEntity recipe) {

		if (Boolean.TRUE.equals(recipe.getCurrent())) {

			return "CURRENT";
		}

		if (recipe.getActivatedAt() != null) {

			return "RETIRED";
		}

		return "DRAFT";
	}
}