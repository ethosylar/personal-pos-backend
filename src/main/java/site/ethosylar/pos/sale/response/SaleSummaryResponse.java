package site.ethosylar.pos.sale.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaleSummaryResponse {

	private Long id;

	private String saleNumber;

	private String statusCode;

	private String statusName;

	private Long cashierUserId;

	private String cashierUsername;

	private OffsetDateTime soldAt;

	private BigDecimal subtotal;

	private BigDecimal discountAmount;

	private BigDecimal taxAmount;

	private BigDecimal totalAmount;

	private BigDecimal totalCostSnapshot;

	private BigDecimal grossProfitSnapshot;

	private OffsetDateTime voidedAt;

	private String voidReason;

	private OffsetDateTime createdAt;
	
	private Integer itemCount;
	
	private BigDecimal totalCost;
	
	private BigDecimal grossProfit;
}