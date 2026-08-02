package site.ethosylar.pos.recipe.model;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecipeCostingResult {

	private List<RecipeItemCostResult> items;

	private BigDecimal batchCost;

	private BigDecimal costPerPortion;

	private Boolean allCostsAvailable;

	private List<String> warnings;
}