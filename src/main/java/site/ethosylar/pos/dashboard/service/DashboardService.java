package site.ethosylar.pos.dashboard.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.dashboard.projection.MonthlySalesProjection;
import site.ethosylar.pos.dashboard.projection.TopProductProjection;
import site.ethosylar.pos.dashboard.response.DashboardSummaryResponse;
import site.ethosylar.pos.dashboard.response.MonthlySalesTrendResponse;
import site.ethosylar.pos.dashboard.response.TopProductResponse;
import site.ethosylar.pos.expense.repository.ExpenseRepository;
import site.ethosylar.pos.payroll.repository.StaffMonthlyCostRepository;
import site.ethosylar.pos.sale.repository.SaleItemRepository;
import site.ethosylar.pos.sale.repository.SaleRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

	private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Kuala_Lumpur");

	private final SaleRepository saleRepository;

	private final SaleItemRepository saleItemRepository;

	private final ExpenseRepository expenseRepository;

	private final StaffMonthlyCostRepository payrollRepository;

	public DashboardSummaryResponse getSummary(LocalDate requestedDate) {

		LocalDate date = requestedDate != null ? requestedDate : LocalDate.now(BUSINESS_ZONE);

		OffsetDateTime dayStart = date.atStartOfDay(BUSINESS_ZONE).toOffsetDateTime();

		OffsetDateTime dayEnd = date.plusDays(1).atStartOfDay(BUSINESS_ZONE).toOffsetDateTime();

		YearMonth month = YearMonth.from(date);

		LocalDate monthStartDate = month.atDay(1);

		LocalDate monthEndDate = month.atEndOfMonth();

		OffsetDateTime monthStart = monthStartDate.atStartOfDay(BUSINESS_ZONE).toOffsetDateTime();

		OffsetDateTime monthEnd = month.plusMonths(1).atDay(1).atStartOfDay(BUSINESS_ZONE).toOffsetDateTime();

		// =========================================================
		// TODAY
		// =========================================================

		long todaySales = saleRepository
				.countByStatus_CodeIgnoreCaseAndSoldAtGreaterThanEqualAndSoldAtLessThan("COMPLETED", dayStart, dayEnd);

		BigDecimal todayRevenue = zero(saleRepository.calculateNetSalesRevenue("COMPLETED", dayStart, dayEnd));

		BigDecimal todayTax = zero(saleRepository.calculateTaxCollected("COMPLETED", dayStart, dayEnd));

		BigDecimal todayCogs = zero(saleRepository.calculateCogs("COMPLETED", dayStart, dayEnd));

		BigDecimal todayGrossProfit = todayRevenue.subtract(todayCogs);

		// =========================================================
		// MONTH SALES
		// =========================================================

		long monthSales = saleRepository.countByStatus_CodeIgnoreCaseAndSoldAtGreaterThanEqualAndSoldAtLessThan(
				"COMPLETED", monthStart, monthEnd);

		BigDecimal monthRevenue = zero(saleRepository.calculateNetSalesRevenue("COMPLETED", monthStart, monthEnd));

		BigDecimal monthTax = zero(saleRepository.calculateTaxCollected("COMPLETED", monthStart, monthEnd));

		BigDecimal monthCogs = zero(saleRepository.calculateCogs("COMPLETED", monthStart, monthEnd));

		BigDecimal monthGrossProfit = monthRevenue.subtract(monthCogs);

		// =========================================================
		// EXPENSES
		// =========================================================

		long expenseCount = expenseRepository.countByStatus_CodeIgnoreCaseAndExpenseDateBetween("POSTED",
				monthStartDate, monthEndDate);

		BigDecimal expenses = zero(expenseRepository.calculatePostedExpenses("POSTED", monthStartDate, monthEndDate));

		// =========================================================
		// PAYROLL
		// =========================================================

		Short year = (short) month.getYear();

		Short monthNumber = (short) month.getMonthValue();

		long payrollCount = payrollRepository
				.countByStatus_CodeIgnoreCaseAndCurrentRecordTrueAndPeriodYearAndPeriodMonth("POSTED", year,
						monthNumber);

		BigDecimal staffCost = zero(payrollRepository.calculatePostedStaffCost("POSTED", year, monthNumber));

		// =========================================================
		// OPERATING RESULT
		// =========================================================

		BigDecimal operatingProfit = monthGrossProfit.subtract(expenses).subtract(staffCost);

		return DashboardSummaryResponse.builder()

				.asOfDate(date)

				.todaySalesCount(Math.toIntExact(todaySales))

				.todayRevenue(money(todayRevenue))

				.todayTaxCollected(money(todayTax))

				.todayCogs(cost(todayCogs))

				.todayGrossProfit(cost(todayGrossProfit))

				.monthSalesCount(Math.toIntExact(monthSales))

				.monthRevenue(money(monthRevenue))

				.monthTaxCollected(money(monthTax))

				.monthCogs(cost(monthCogs))

				.monthGrossProfit(cost(monthGrossProfit))

				.monthExpenseCount(Math.toIntExact(expenseCount))

				.monthOperatingExpenses(money(expenses))

				.monthPayrollCount(Math.toIntExact(payrollCount))

				.monthStaffCost(money(staffCost))

				.monthOperatingProfit(cost(operatingProfit))

				.build();
	}

	private BigDecimal zero(BigDecimal value) {

		return value != null ? value : BigDecimal.ZERO;
	}

	private BigDecimal money(BigDecimal value) {

		return zero(value).setScale(4, RoundingMode.HALF_UP);
	}

	private BigDecimal cost(BigDecimal value) {

		return zero(value).setScale(6, RoundingMode.HALF_UP);
	}

	public List<MonthlySalesTrendResponse> getSalesTrend(int year) {

		OffsetDateTime start = LocalDate.of(year, 1, 1).atStartOfDay(BUSINESS_ZONE).toOffsetDateTime();

		OffsetDateTime end = LocalDate.of(year + 1, 1, 1).atStartOfDay(BUSINESS_ZONE).toOffsetDateTime();

		List<MonthlySalesProjection> data = saleRepository.findMonthlySalesTrend(start, end);

		Map<Integer, MonthlySalesProjection> byMonth = data.stream()
				.collect(Collectors.toMap(MonthlySalesProjection::getMonthNumber, Function.identity()));

		List<MonthlySalesTrendResponse> result = new ArrayList<>();

		/*
		 * Return all 12 months so Angular does not need to fill missing months itself.
		 */
		for (int month = 1; month <= 12; month++) {

			MonthlySalesProjection row = byMonth.get(month);

			BigDecimal revenue = row != null ? zero(row.getRevenue()) : BigDecimal.ZERO;

			BigDecimal cogs = row != null ? zero(row.getCogs()) : BigDecimal.ZERO;

			result.add(MonthlySalesTrendResponse.builder()

					.year(year)

					.month(month)

					.salesCount(row != null ? Math.toIntExact(row.getSalesCount()) : 0)

					.revenue(money(revenue))

					.cogs(cost(cogs))

					.grossProfit(cost(revenue.subtract(cogs)))

					.build());
		}

		return result;
	}

	public List<TopProductResponse> getTopProducts(int year, int month, int limit) {

		YearMonth period = YearMonth.of(year, month);

		OffsetDateTime start = period.atDay(1).atStartOfDay(BUSINESS_ZONE).toOffsetDateTime();

		OffsetDateTime end = period.plusMonths(1).atDay(1).atStartOfDay(BUSINESS_ZONE).toOffsetDateTime();

		int safeLimit = Math.min(Math.max(limit, 1), 100);

		return saleItemRepository.findTopProducts(start, end, PageRequest.of(0, safeLimit)).stream()
				.map(this::toTopProduct).toList();
	}

	private TopProductResponse toTopProduct(TopProductProjection row) {

		return TopProductResponse.builder()

				.productId(row.getProductId())

				.productCode(row.getProductCode())

				.productName(row.getProductName())

				.quantitySold(zero(row.getQuantitySold()))

				.revenue(money(row.getRevenue()))

				.cogs(cost(row.getCogs()))

				.grossProfit(cost(row.getGrossProfit()))

				.build();
	}

}