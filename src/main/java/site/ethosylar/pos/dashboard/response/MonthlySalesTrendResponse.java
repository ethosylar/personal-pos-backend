package site.ethosylar.pos.dashboard.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthlySalesTrendResponse {

	private Integer year;

	private Integer month;

	private Integer salesCount;

	private BigDecimal revenue;

	private BigDecimal cogs;

	private BigDecimal grossProfit;
}