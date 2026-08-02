package site.ethosylar.pos.recipe.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecipeItemCostResult {

    private Long recipeItemId;

    private BigDecimal effectiveQuantity;

    private BigDecimal unitCost;

    private BigDecimal lineCost;

    private Boolean costAvailable;

    private String warning;
}