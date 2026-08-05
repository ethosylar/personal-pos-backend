package site.ethosylar.pos.dashboard.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TopProductResponse {

	private Long productId;

	private String productCode;

	private String productName;

	private BigDecimal quantitySold;

	private BigDecimal revenue;

	private BigDecimal cogs;

	private BigDecimal grossProfit;
}