package site.ethosylar.pos.accounting.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.accounting.entity.MonthlyAccountingEntity;
import site.ethosylar.pos.accounting.entity.MonthlyAccountingStatusEntity;
import site.ethosylar.pos.accounting.mapper.MonthlyAccountingMapper;
import site.ethosylar.pos.accounting.repository.MonthlyAccountingRepository;
import site.ethosylar.pos.accounting.repository.MonthlyAccountingStatusRepository;
import site.ethosylar.pos.accounting.request.GenerateMonthlyAccountingRequest;
import site.ethosylar.pos.accounting.request.VoidMonthlyAccountingRequest;
import site.ethosylar.pos.accounting.response.MonthlyAccountingResponse;
import site.ethosylar.pos.audit.request.AuditLogRequest;
import site.ethosylar.pos.audit.service.AuditService;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.DuplicateResourceException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.expense.repository.ExpenseRepository;
import site.ethosylar.pos.payroll.repository.StaffMonthlyCostRepository;
import site.ethosylar.pos.sale.repository.SaleRepository;
import site.ethosylar.pos.user.entity.UserEntity;
import site.ethosylar.pos.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class MonthlyAccountingService {

	private final MonthlyAccountingRepository accountingRepository;

	private final MonthlyAccountingStatusRepository statusRepository;

	private final SaleRepository saleRepository;

	private final ExpenseRepository expenseRepository;

	private final StaffMonthlyCostRepository payrollRepository;

	private final UserRepository userRepository;

	private final MonthlyAccountingNumberService numberService;

	private final MonthlyAccountingMapper mapper;

	private final AuditService auditService;

	public MonthlyAccountingResponse generate(GenerateMonthlyAccountingRequest request) {

		Short year = request.getPeriodYear().shortValue();

		Short month = request.getPeriodMonth().shortValue();

		if (accountingRepository.existsByPeriodYearAndPeriodMonthAndCurrentRecordTrue(year, month)) {

			throw new DuplicateResourceException("A current accounting record already exists for this period.");
		}

		MonthlyAccountingEntity accounting = new MonthlyAccountingEntity();

		accounting.setAccountingNumber(numberService.nextNumber(request.getPeriodYear(), request.getPeriodMonth()));

		accounting.setPeriodYear(year);
		accounting.setPeriodMonth(month);

		accounting.setStatus(getStatus("DRAFT"));

		accounting.setNotes(normalize(request.getNotes()));

		accounting.setCurrentRecord(true);

		calculate(accounting);

		accounting = accountingRepository.save(accounting);

		auditService.log(AuditLogRequest.builder().actionCode("CREATE").moduleCode("ACCOUNTING")
				.entityType("MONTHLY_ACCOUNTING").entityId(accounting.getId())
				.description("Monthly accounting generated.").afterData(snapshot(accounting)).build());

		return mapper.toResponse(accounting);
	}

	private void calculate(MonthlyAccountingEntity accounting) {

		int year = accounting.getPeriodYear().intValue();

		int month = accounting.getPeriodMonth().intValue();

		YearMonth period = YearMonth.of(year, month);

		LocalDate fromDate = period.atDay(1);

		LocalDate toDate = period.atEndOfMonth();

		/*
		 * Sales use timestamps.
		 *
		 * Start inclusive: 2026-08-01 00:00
		 *
		 * End exclusive: 2026-09-01 00:00
		 */
		ZoneId zone = ZoneId.of("Asia/Kuala_Lumpur");

		OffsetDateTime salesStart = fromDate.atStartOfDay(zone).toOffsetDateTime();

		OffsetDateTime salesEnd = period.plusMonths(1).atDay(1).atStartOfDay(zone).toOffsetDateTime();

		// =========================================================
		// SALES
		// =========================================================

		long salesCount = saleRepository.countByStatus_CodeIgnoreCaseAndSoldAtGreaterThanEqualAndSoldAtLessThan(
				"COMPLETED", salesStart, salesEnd);

		BigDecimal revenue = saleRepository.calculateNetSalesRevenue("COMPLETED", salesStart, salesEnd);

		BigDecimal tax = saleRepository.calculateTaxCollected("COMPLETED", salesStart, salesEnd);

		BigDecimal cogs = saleRepository.calculateCogs("COMPLETED", salesStart, salesEnd);

		// =========================================================
		// EXPENSES
		// =========================================================

		long expenseCount = expenseRepository.countByStatus_CodeIgnoreCaseAndExpenseDateBetween("POSTED", fromDate,
				toDate);

		BigDecimal expenses = expenseRepository.calculatePostedExpenses("POSTED", fromDate, toDate);

		// =========================================================
		// PAYROLL
		// =========================================================

		long payrollCount = payrollRepository
				.countByStatus_CodeIgnoreCaseAndCurrentRecordTrueAndPeriodYearAndPeriodMonth("POSTED",
						accounting.getPeriodYear(), accounting.getPeriodMonth());

		BigDecimal staffCost = payrollRepository.calculatePostedStaffCost("POSTED", accounting.getPeriodYear(),
				accounting.getPeriodMonth());

		// =========================================================
		// PROFIT
		// =========================================================

		BigDecimal grossProfit = revenue.subtract(cogs).setScale(6, RoundingMode.HALF_UP);

		BigDecimal operatingProfit = grossProfit.subtract(expenses).subtract(staffCost).setScale(6,
				RoundingMode.HALF_UP);

		accounting.setSalesCount(Math.toIntExact(salesCount));

		accounting.setNetSalesRevenue(revenue.setScale(4, RoundingMode.HALF_UP));

		accounting.setTaxCollected(tax.setScale(4, RoundingMode.HALF_UP));

		accounting.setCogs(cogs.setScale(6, RoundingMode.HALF_UP));

		accounting.setGrossProfit(grossProfit);

		accounting.setPostedExpenseCount(Math.toIntExact(expenseCount));

		accounting.setOperatingExpenses(expenses.setScale(4, RoundingMode.HALF_UP));

		accounting.setPostedPayrollCount(Math.toIntExact(payrollCount));

		accounting.setStaffCost(staffCost.setScale(4, RoundingMode.HALF_UP));

		accounting.setOperatingProfit(operatingProfit);

		accounting.setCalculatedAt(OffsetDateTime.now());

		accounting.setCalculatedByUser(getCurrentUser());
	}

	@Transactional(readOnly = true)
	public MonthlyAccountingResponse getById(Long id) {

		MonthlyAccountingEntity accounting = accountingRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Monthly accounting record not found."));

		return mapper.toResponse(accounting);
	}

	public MonthlyAccountingResponse refresh(Long id) {

		MonthlyAccountingEntity accounting = getForUpdate(id);

		ensureDraft(accounting);

		Map<String, Object> before = snapshot(accounting);

		calculate(accounting);

		accountingRepository.save(accounting);

		auditService.log(
				AuditLogRequest.builder().actionCode("UPDATE").moduleCode("ACCOUNTING").entityType("MONTHLY_ACCOUNTING")
						.entityId(accounting.getId()).description("Monthly accounting refreshed.").beforeData(before)
						.afterData(snapshot(accounting)).build());

		return mapper.toResponse(accounting);
	}

	public MonthlyAccountingResponse post(Long id) {

		MonthlyAccountingEntity accounting = getForUpdate(id);

		ensureDraft(accounting);

		Map<String, Object> before = snapshot(accounting);
		/*
		 * Recalculate once immediately before posting, ensuring the final snapshot uses
		 * the latest financial transactions.
		 */
		calculate(accounting);

		UserEntity user = getCurrentUser();

		accounting.setStatus(getStatus("POSTED"));

		accounting.setPostedAt(OffsetDateTime.now());

		accounting.setPostedByUser(user);

		accountingRepository.save(accounting);

		auditService.log(AuditLogRequest.builder().actionCode("STATUS_CHANGE").moduleCode("ACCOUNTING")
				.entityType("MONTHLY_ACCOUNTING").entityId(accounting.getId()).description("Monthly accounting posted.")
				.beforeData(before).afterData(snapshot(accounting)).build());

		return mapper.toResponse(accounting);
	}

	public MonthlyAccountingResponse voidAccounting(Long id, VoidMonthlyAccountingRequest request) {

		MonthlyAccountingEntity accounting = getForUpdate(id);

		if (!"POSTED".equalsIgnoreCase(accounting.getStatus().getCode())) {

			throw new BusinessRuleException("Only posted accounting records can be voided.");
		}

		Map<String, Object> before = snapshot(accounting);

		accounting.setStatus(getStatus("VOIDED"));

		accounting.setVoidedAt(OffsetDateTime.now());

		accounting.setVoidedByUser(getCurrentUser());

		accounting.setVoidReason(request.getReason().trim());

		accounting.setCurrentRecord(false);

		accountingRepository.save(accounting);

		auditService.log(AuditLogRequest.builder().actionCode("STATUS_CHANGE").moduleCode("ACCOUNTING")
				.entityType("MONTHLY_ACCOUNTING").entityId(accounting.getId()).description("Monthly accounting voided.")
				.beforeData(before).afterData(snapshot(accounting)).build());

		return mapper.toResponse(accounting);
	}

	private MonthlyAccountingEntity getForUpdate(Long id) {

		return accountingRepository.findByIdForUpdate(id)
				.orElseThrow(() -> new ResourceNotFoundException("Monthly accounting record not found."));
	}

	private MonthlyAccountingStatusEntity getStatus(String code) {

		return statusRepository.findByCodeIgnoreCase(code)
				.orElseThrow(() -> new ResourceNotFoundException("Monthly accounting status not found: " + code));
	}

	private void ensureDraft(MonthlyAccountingEntity accounting) {

		if (!"DRAFT".equalsIgnoreCase(accounting.getStatus().getCode())) {

			throw new BusinessRuleException("Only draft accounting records can be refreshed or posted.");
		}
	}

	private UserEntity getCurrentUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {

			return null;
		}

		return userRepository.findByUsernameIgnoreCase(authentication.getName()).orElse(null);
	}

	private String normalize(String value) {

		if (value == null || value.isBlank()) {

			return null;
		}

		return value.trim();
	}

	private Map<String, Object> snapshot(MonthlyAccountingEntity accounting) {

		Map<String, Object> data = new LinkedHashMap<>();

		data.put("accountingNumber", accounting.getAccountingNumber());

		data.put("periodYear", accounting.getPeriodYear());

		data.put("periodMonth", accounting.getPeriodMonth());

		data.put("status", accounting.getStatus().getCode());

		data.put("salesCount", accounting.getSalesCount());

		data.put("netSalesRevenue", accounting.getNetSalesRevenue());

		data.put("taxCollected", accounting.getTaxCollected());

		data.put("cogs", accounting.getCogs());

		data.put("grossProfit", accounting.getGrossProfit());

		data.put("operatingExpenses", accounting.getOperatingExpenses());

		data.put("staffCost", accounting.getStaffCost());

		data.put("operatingProfit", accounting.getOperatingProfit());

		data.put("calculatedAt", accounting.getCalculatedAt());

		data.put("postedAt", accounting.getPostedAt());

		data.put("voidedAt", accounting.getVoidedAt());

		data.put("voidReason", accounting.getVoidReason());

		data.put("currentRecord", accounting.getCurrentRecord());

		return data;
	}
}
