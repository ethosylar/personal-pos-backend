package site.ethosylar.pos.dashboard.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardSummaryResponse {

	private LocalDate asOfDate;

	// TODAY

	private Integer todaySalesCount;

	private BigDecimal todayRevenue;

	private BigDecimal todayTaxCollected;

	private BigDecimal todayCogs;

	private BigDecimal todayGrossProfit;

	// CURRENT MONTH

	private Integer monthSalesCount;

	private BigDecimal monthRevenue;

	private BigDecimal monthTaxCollected;

	private BigDecimal monthCogs;

	private BigDecimal monthGrossProfit;

	// EXPENSE

	private Integer monthExpenseCount;

	private BigDecimal monthOperatingExpenses;

	// PAYROLL

	private Integer monthPayrollCount;

	private BigDecimal monthStaffCost;

	// RESULT

	private BigDecimal monthOperatingProfit;
}