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
import site.ethosylar.pos.dashboard.response.InventoryStockAlertResponse;
import site.ethosylar.pos.dashboard.response.InventorySummaryResponse;
import site.ethosylar.pos.dashboard.response.MonthlySalesTrendResponse;
import site.ethosylar.pos.dashboard.response.RecentInventoryMovementResponse;
import site.ethosylar.pos.dashboard.response.TopProductResponse;
import site.ethosylar.pos.expense.repository.ExpenseRepository;
import site.ethosylar.pos.ingredient.service.IngredientCostService;
import site.ethosylar.pos.inventory.projection.InventoryStockProjection;
import site.ethosylar.pos.inventory.projection.RecentInventoryMovementProjection;
import site.ethosylar.pos.inventory.repository.InventoryMovementRepository;
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

	private final InventoryMovementRepository inventoryMovementRepository;

	private final IngredientCostService ingredientCostService;

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

	private String determineStockStatus(InventoryStockProjection stock) {

		/*
		 * No configured stock policy.
		 */
		if (!Boolean.TRUE.equals(stock.getAlertEnabled())) {

			return "UNCONFIGURED";
		}

		BigDecimal currentStock = zero(stock.getCurrentStock());

		BigDecimal reorderLevel = zero(stock.getReorderLevel());

		if (currentStock.compareTo(BigDecimal.ZERO) <= 0) {

			return "OUT_OF_STOCK";
		}

		if (currentStock.compareTo(reorderLevel) <= 0) {

			return "LOW_STOCK";
		}

		return "HEALTHY";
	}

	private BigDecimal calculateSuggestedReorder(InventoryStockProjection stock) {

		if (!Boolean.TRUE.equals(stock.getAlertEnabled())) {

			return BigDecimal.ZERO;
		}

		BigDecimal target = stock.getTargetStockLevel();

		if (target == null) {
			return BigDecimal.ZERO;
		}

		BigDecimal current = zero(stock.getCurrentStock());

		BigDecimal suggested = target.subtract(current);

		if (suggested.compareTo(BigDecimal.ZERO) < 0) {

			return BigDecimal.ZERO;
		}

		return suggested.setScale(4, RoundingMode.HALF_UP);
	}

	public InventorySummaryResponse getInventorySummary() {

		List<InventoryStockProjection> stocks = inventoryMovementRepository.findInventoryStockIntelligence();

		int healthy = 0;
		int low = 0;
		int out = 0;
		int unconfigured = 0;

		BigDecimal totalInventoryValue = BigDecimal.ZERO;

		for (InventoryStockProjection stock : stocks) {

			String status = determineStockStatus(stock);

			switch (status) {

			case "HEALTHY":
				healthy++;
				break;

			case "LOW_STOCK":
				low++;
				break;

			case "OUT_OF_STOCK":
				out++;
				break;

			default:
				unconfigured++;
				break;
			}

			BigDecimal currentStock = zero(stock.getCurrentStock());

			/*
			 * Negative stock should not create negative inventory asset value.
			 */
			BigDecimal valuatedStock = currentStock.max(BigDecimal.ZERO);

			BigDecimal unitCost = zero(ingredientCostService.getCurrentUnitCost(stock.getIngredientId()));

			totalInventoryValue = totalInventoryValue.add(valuatedStock.multiply(unitCost));
		}

		return InventorySummaryResponse.builder()

				.totalIngredients(stocks.size())

				.healthyCount(healthy)

				.lowStockCount(low)

				.outOfStockCount(out)

				.unconfiguredCount(unconfigured)

				.alertCount(low + out)

				.totalInventoryValue(totalInventoryValue.setScale(6, RoundingMode.HALF_UP))

				.build();
	}

	public List<InventoryStockAlertResponse> getStockAlerts() {

		return inventoryMovementRepository.findInventoryStockIntelligence().stream()

				.filter(stock -> {

					String status = determineStockStatus(stock);

					return "LOW_STOCK".equals(status) || "OUT_OF_STOCK".equals(status);
				})

				/*
				 * OUT_OF_STOCK first, then LOW_STOCK.
				 */
				.sorted((a, b) -> {

					String statusA = determineStockStatus(a);

					String statusB = determineStockStatus(b);

					int priorityA = "OUT_OF_STOCK".equals(statusA) ? 0 : 1;

					int priorityB = "OUT_OF_STOCK".equals(statusB) ? 0 : 1;

					int compare = Integer.compare(priorityA, priorityB);

					if (compare != 0) {
						return compare;
					}

					return zero(a.getCurrentStock()).compareTo(zero(b.getCurrentStock()));
				})

				.map(this::toInventoryStockResponse)

				.toList();
	}

	public List<InventoryStockAlertResponse> getInventoryStock() {

		return inventoryMovementRepository.findInventoryStockIntelligence().stream().map(this::toInventoryStockResponse)
				.toList();
	}

	private InventoryStockAlertResponse toInventoryStockResponse(InventoryStockProjection stock) {

		BigDecimal currentStock = zero(stock.getCurrentStock()).setScale(4, RoundingMode.HALF_UP);

		BigDecimal unitCost = zero(ingredientCostService.getCurrentUnitCost(stock.getIngredientId())).setScale(6,
				RoundingMode.HALF_UP);

		BigDecimal inventoryValue = currentStock.multiply(unitCost).setScale(6, RoundingMode.HALF_UP);

		return InventoryStockAlertResponse.builder()

				.ingredientId(stock.getIngredientId())

				.ingredientCode(stock.getIngredientCode())

				.ingredientName(stock.getIngredientName())

				.currentStock(currentStock)

				.reorderLevel(zero(stock.getReorderLevel()).setScale(4, RoundingMode.HALF_UP))

				.targetStockLevel(stock.getTargetStockLevel())

				.stockStatus(determineStockStatus(stock))

				.suggestedReorderQuantity(calculateSuggestedReorder(stock))

				.unitCost(unitCost)

				.inventoryValue(inventoryValue)

				.alertEnabled(stock.getAlertEnabled())

				.build();
	}

	public List<RecentInventoryMovementResponse> getRecentInventoryMovements(int limit) {

		int safeLimit = Math.min(Math.max(limit, 1), 100);

		return inventoryMovementRepository.findRecentMovements(PageRequest.of(0, safeLimit)).stream()
				.map(this::toRecentMovement).toList();
	}

	private RecentInventoryMovementResponse toRecentMovement(RecentInventoryMovementProjection row) {

		BigDecimal quantity = zero(row.getQuantity());

		BigDecimal direction = BigDecimal.valueOf(row.getDirection());

		BigDecimal signedQuantity = quantity.multiply(direction).setScale(4, RoundingMode.HALF_UP);

		return RecentInventoryMovementResponse.builder()

				.id(row.getId())

				.ingredientId(row.getIngredientId())

				.ingredientCode(row.getIngredientCode())

				.ingredientName(row.getIngredientName())

				.movementTypeCode(row.getMovementTypeCode())

				.movementTypeName(row.getMovementTypeName())

				.direction(row.getDirection())

				.quantity(quantity)

				.signedQuantity(signedQuantity)

				.unitCost(row.getUnitCost())

				.referenceType(row.getReferenceType())

				.referenceId(row.getReferenceId())

				.referenceNo(row.getReferenceNo())

				.reason(row.getReason())

				.occurredAt(row.getOccurredAt())

				.createdByUserId(row.getCreatedByUserId())

				.createdByUsername(row.getCreatedByUsername())

				.build();
	}

}