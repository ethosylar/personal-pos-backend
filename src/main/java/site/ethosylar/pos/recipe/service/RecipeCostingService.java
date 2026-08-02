package site.ethosylar.pos.recipe.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.ingredient.entity.IngredientSupplierEntity;
import site.ethosylar.pos.ingredient.repository.IngredientSupplierRepository;
import site.ethosylar.pos.recipe.entity.RecipeEntity;
import site.ethosylar.pos.recipe.entity.RecipeItemEntity;
import site.ethosylar.pos.recipe.model.RecipeCostingResult;
import site.ethosylar.pos.recipe.model.RecipeItemCostResult;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeCostingService {

	private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

	private final IngredientSupplierRepository ingredientSupplierRepository;

	public RecipeCostingResult calculate(RecipeEntity recipe, List<RecipeItemEntity> items) {

		List<RecipeItemCostResult> itemResults = new ArrayList<>();

		List<String> warnings = new ArrayList<>();

		BigDecimal batchCost = BigDecimal.ZERO;

		boolean allCostsAvailable = true;

		for (RecipeItemEntity item : items) {

			Optional<IngredientSupplierEntity> preferredSupplier = ingredientSupplierRepository
					.findFirstByIngredient_IdAndPreferredTrueAndActiveTrue(item.getIngredient().getId());

			BigDecimal effectiveQuantity = calculateEffectiveQuantity(item);

			if (preferredSupplier.isEmpty()) {

				allCostsAvailable = false;

				String warning = "No active preferred supplier found for ingredient " + item.getIngredient().getCode()
						+ ".";

				warnings.add(warning);

				itemResults.add(
						RecipeItemCostResult.builder().recipeItemId(item.getId()).effectiveQuantity(effectiveQuantity)
								.unitCost(null).lineCost(null).costAvailable(false).warning(warning).build());

				continue;
			}

			IngredientSupplierEntity supplier = preferredSupplier.get();

			BigDecimal unitCost = supplier.getPurchasePrice().divide(supplier.getBaseQuantity(), 6,
					RoundingMode.HALF_UP);

			BigDecimal lineCost = effectiveQuantity.multiply(unitCost).setScale(6, RoundingMode.HALF_UP);

			batchCost = batchCost.add(lineCost);

			itemResults
					.add(RecipeItemCostResult.builder().recipeItemId(item.getId()).effectiveQuantity(effectiveQuantity)
							.unitCost(unitCost).lineCost(lineCost).costAvailable(true).warning(null).build());
		}

		BigDecimal costPerPortion = null;

		if (allCostsAvailable) {

			costPerPortion = batchCost.divide(recipe.getYieldQuantity(), 6, RoundingMode.HALF_UP);
		}

		return RecipeCostingResult.builder().items(itemResults)
				.batchCost(allCostsAvailable ? batchCost.setScale(6, RoundingMode.HALF_UP) : null)
				.costPerPortion(costPerPortion).allCostsAvailable(allCostsAvailable).warnings(warnings).build();
	}

	private BigDecimal calculateEffectiveQuantity(RecipeItemEntity item) {

		BigDecimal wasteMultiplier = BigDecimal.ONE
				.add(item.getWastePercent().divide(ONE_HUNDRED, 8, RoundingMode.HALF_UP));

		return item.getQuantity().multiply(wasteMultiplier).setScale(6, RoundingMode.HALF_UP);
	}
}