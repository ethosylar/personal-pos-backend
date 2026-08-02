package site.ethosylar.pos.recipe.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecipeDetailResponse {

	private Long id;

	private Long productId;

	private String productCode;

	private String productName;

	private BigDecimal productSellingPrice;

	private Integer versionNo;

	private String lifecycleStatus;

	private BigDecimal yieldQuantity;

	private Boolean current;

	/*
	 * Live/current supplier costing.
	 */
	private BigDecimal currentBatchCost;

	private BigDecimal currentCostPerPortion;

	private BigDecimal currentGrossProfit;

	private BigDecimal currentMarginPercent;

	/*
	 * Historical cost captured when activated.
	 */
	private BigDecimal activatedCostSnapshot;

	private Boolean allCostsAvailable;

	private List<String> costingWarnings;

	private OffsetDateTime effectiveFrom;

	private OffsetDateTime effectiveTo;

	private OffsetDateTime activatedAt;

	private String notes;

	private List<RecipeItemResponse> items;

	private OffsetDateTime createdAt;

	private OffsetDateTime updatedAt;
}