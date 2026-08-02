package site.ethosylar.pos.recipe.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecipeSummaryResponse {

	private Long id;

	private Long productId;

	private String productCode;

	private String productName;

	private Integer versionNo;

	private String lifecycleStatus;

	private BigDecimal yieldQuantity;

	private Boolean current;

	private BigDecimal activatedCostSnapshot;

	private OffsetDateTime effectiveFrom;

	private OffsetDateTime effectiveTo;

	private OffsetDateTime activatedAt;

	private OffsetDateTime createdAt;

	private OffsetDateTime updatedAt;
}