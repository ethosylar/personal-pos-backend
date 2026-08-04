package site.ethosylar.pos.payroll.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.request.AuditLogRequest;
import site.ethosylar.pos.audit.service.AuditService;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.DuplicateResourceException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.payroll.entity.StaffMonthlyCostEntity;
import site.ethosylar.pos.payroll.entity.StaffMonthlyCostStatusEntity;
import site.ethosylar.pos.payroll.mapper.PayrollMapper;
import site.ethosylar.pos.payroll.repository.StaffMonthlyCostRepository;
import site.ethosylar.pos.payroll.repository.StaffMonthlyCostStatusRepository;
import site.ethosylar.pos.payroll.request.CreateStaffMonthlyCostRequest;
import site.ethosylar.pos.payroll.request.UpdateStaffMonthlyCostRequest;
import site.ethosylar.pos.payroll.request.VoidStaffMonthlyCostRequest;
import site.ethosylar.pos.payroll.response.StaffMonthlyCostResponse;
import site.ethosylar.pos.staff.entity.StaffEntity;
import site.ethosylar.pos.staff.entity.StaffSalaryRateEntity;
import site.ethosylar.pos.staff.entity.StaffStatusEntity;
import site.ethosylar.pos.staff.repository.StaffRepository;
import site.ethosylar.pos.staff.repository.StaffSalaryRateRepository;
import site.ethosylar.pos.user.entity.UserEntity;
import site.ethosylar.pos.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffMonthlyCostService {

	private final StaffMonthlyCostRepository costRepository;

	private final StaffMonthlyCostStatusRepository statusRepository;

	private final StaffRepository staffRepository;

	private final StaffSalaryRateRepository salaryRateRepository;

	private final UserRepository userRepository;

	private final StaffMonthlyCostNumberService numberService;

	private final PayrollMapper payrollMapper;

	private final AuditService auditService;

	public StaffMonthlyCostResponse create(CreateStaffMonthlyCostRequest request) {

		Short year = request.getPeriodYear().shortValue();

		Short month = request.getPeriodMonth().shortValue();

		if (costRepository.existsByStaff_IdAndPeriodYearAndPeriodMonthAndCurrentRecordTrue(request.getStaffId(), year,
				month)) {

			throw new DuplicateResourceException(
					"A current payroll record already exists for this staff member and period.");
		}

		StaffEntity staff = staffRepository.findById(request.getStaffId())
				.orElseThrow(() -> new ResourceNotFoundException("Staff not found."));

		YearMonth period = YearMonth.of(request.getPeriodYear(), request.getPeriodMonth());

		validateEmploymentPeriod(staff, period);

		StaffSalaryRateEntity salaryRate = resolveSalaryRate(staff.getId(), period);

		BigDecimal basePay = calculateBasePay(salaryRate, request.getUnitsWorked());

		PayrollAmounts amounts = calculateAmounts(basePay, salaryRate.getFixedAllowance(),
				request.getAdditionalAllowance(), request.getOvertimePay(), request.getBonusAmount(),
				request.getDeductionAmount(), request.getEmployerContribution());

		StaffMonthlyCostEntity cost = new StaffMonthlyCostEntity();

		cost.setCostNumber(numberService.nextNumber());

		cost.setStaff(staff);

		cost.setSalaryRate(salaryRate);

		cost.setStatus(getStatus("DRAFT"));

		cost.setPeriodYear(year);
		cost.setPeriodMonth(month);

		cost.setPayTypeCodeSnapshot(salaryRate.getPayType().getCode());

		cost.setBaseRateSnapshot(salaryRate.getBaseRate());

		cost.setFixedAllowanceSnapshot(salaryRate.getFixedAllowance());

		cost.setUnitsWorked(request.getUnitsWorked());

		cost.setBasePay(basePay);

		cost.setAdditionalAllowance(request.getAdditionalAllowance());

		cost.setOvertimePay(request.getOvertimePay());

		cost.setBonusAmount(request.getBonusAmount());

		cost.setDeductionAmount(request.getDeductionAmount());

		cost.setEmployerContribution(request.getEmployerContribution());

		applyAmounts(cost, amounts);

		cost.setNotes(normalize(request.getNotes()));

		cost.setCreatedByUser(getCurrentUser());

		cost.setCurrentRecord(true);

		cost = costRepository.save(cost);

		auditService.log(AuditLogRequest.builder().actionCode("CREATE").moduleCode("PAYROLL")
				.entityType("STAFF_MONTHLY_COST").entityId(cost.getId()).description("Monthly staff cost created.")
				.afterData(snapshot(cost)).build());

		return payrollMapper.toResponse(cost);
	}

	private StaffSalaryRateEntity resolveSalaryRate(Long staffId, YearMonth period) {

		LocalDate periodStart = period.atDay(1);

		LocalDate periodEnd = period.atEndOfMonth();

		List<StaffSalaryRateEntity> rates = salaryRateRepository.findEffectiveRates(staffId, periodStart);

		if (rates.isEmpty()) {

			throw new BusinessRuleException("No salary rate is configured for this staff member for " + period + ".");
		}

		if (rates.size() > 1) {

			throw new BusinessRuleException("Multiple overlapping salary rates were found for this staff member.");
		}

		StaffSalaryRateEntity rate = rates.get(0);

		/*
		 * For the MVP, one salary rate must cover the entire payroll month.
		 *
		 * This avoids silently calculating a month where salary changed halfway
		 * through.
		 */
		if (rate.getEffectiveTo() != null && rate.getEffectiveTo().isBefore(periodEnd)) {

			throw new BusinessRuleException(
					"Salary rate changes during this payroll month. Mid-month salary proration is not supported yet.");
		}

		return rate;
	}

	private void validateEmploymentPeriod(StaffEntity staff, YearMonth period) {

		LocalDate periodStart = period.atDay(1);

		LocalDate periodEnd = period.atEndOfMonth();

		if (staff.getHireDate().isAfter(periodEnd)) {

			throw new BusinessRuleException("Staff member was not yet employed during this payroll period.");
		}

		if (staff.getTerminationDate() != null && staff.getTerminationDate().isBefore(periodStart)) {

			throw new BusinessRuleException("Staff member was no longer employed during this payroll period.");
		}
	}

	private BigDecimal calculateBasePay(StaffSalaryRateEntity rate, BigDecimal unitsWorked) {

		String payType = rate.getPayType().getCode().toUpperCase();

		BigDecimal baseRate = rate.getBaseRate();

		switch (payType) {

		case "MONTHLY":

			if (unitsWorked != null) {

				throw new BusinessRuleException("Units worked should not be supplied for MONTHLY salary.");
			}

			return baseRate.setScale(4, RoundingMode.HALF_UP);

		case "DAILY":
		case "HOURLY":

			if (unitsWorked == null || unitsWorked.compareTo(BigDecimal.ZERO) <= 0) {

				throw new BusinessRuleException("Units worked is required for " + payType + " salary.");
			}

			return baseRate.multiply(unitsWorked).setScale(4, RoundingMode.HALF_UP);

		default:

			throw new BusinessRuleException("Unsupported salary pay type: " + payType);
		}
	}

	private record PayrollAmounts(BigDecimal grossPay, BigDecimal netPay, BigDecimal totalStaffCost) {
	}

	private PayrollAmounts calculateAmounts(BigDecimal basePay, BigDecimal fixedAllowance,
			BigDecimal additionalAllowance, BigDecimal overtimePay, BigDecimal bonusAmount, BigDecimal deductionAmount,
			BigDecimal employerContribution) {

		BigDecimal grossPay = basePay.add(fixedAllowance).add(additionalAllowance).add(overtimePay).add(bonusAmount)
				.setScale(4, RoundingMode.HALF_UP);

		if (deductionAmount.compareTo(grossPay) > 0) {

			throw new BusinessRuleException("Deduction amount cannot exceed gross pay.");
		}

		BigDecimal netPay = grossPay.subtract(deductionAmount).setScale(4, RoundingMode.HALF_UP);

		BigDecimal totalStaffCost = grossPay.add(employerContribution).setScale(4, RoundingMode.HALF_UP);

		return new PayrollAmounts(grossPay, netPay, totalStaffCost);
	}

	private void applyAmounts(StaffMonthlyCostEntity cost, PayrollAmounts amounts) {

		cost.setGrossPay(amounts.grossPay());

		cost.setNetPay(amounts.netPay());

		cost.setTotalStaffCost(amounts.totalStaffCost());
	}

	@Transactional(readOnly = true)
	public StaffMonthlyCostResponse getById(Long id) {

		StaffMonthlyCostEntity cost = costRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Payroll record not found."));

		return payrollMapper.toResponse(cost);
	}

	public StaffMonthlyCostResponse update(Long id, UpdateStaffMonthlyCostRequest request) {

		StaffMonthlyCostEntity cost = getForUpdate(id);

		ensureDraft(cost);

		Map<String, Object> before = snapshot(cost);

		BigDecimal basePay = calculateBasePayFromSnapshot(cost, request.getUnitsWorked());

		PayrollAmounts amounts = calculateAmounts(basePay, cost.getFixedAllowanceSnapshot(),
				request.getAdditionalAllowance(), request.getOvertimePay(), request.getBonusAmount(),
				request.getDeductionAmount(), request.getEmployerContribution());

		cost.setUnitsWorked(request.getUnitsWorked());

		cost.setBasePay(basePay);

		cost.setAdditionalAllowance(request.getAdditionalAllowance());

		cost.setOvertimePay(request.getOvertimePay());

		cost.setBonusAmount(request.getBonusAmount());

		cost.setDeductionAmount(request.getDeductionAmount());

		cost.setEmployerContribution(request.getEmployerContribution());

		applyAmounts(cost, amounts);

		cost.setNotes(normalize(request.getNotes()));

		costRepository.save(cost);

		auditService.log(AuditLogRequest.builder().actionCode("UPDATE").moduleCode("PAYROLL")
				.entityType("STAFF_MONTHLY_COST").entityId(cost.getId())
				.description("Draft monthly staff cost updated.").beforeData(before).afterData(snapshot(cost)).build());

		return payrollMapper.toResponse(cost);
	}

	private BigDecimal calculateBasePayFromSnapshot(StaffMonthlyCostEntity cost, BigDecimal unitsWorked) {

		String payType = cost.getPayTypeCodeSnapshot().toUpperCase();

		BigDecimal rate = cost.getBaseRateSnapshot();

		if ("MONTHLY".equals(payType)) {

			if (unitsWorked != null) {

				throw new BusinessRuleException("Units worked should not be supplied for MONTHLY salary.");
			}

			return rate.setScale(4, RoundingMode.HALF_UP);
		}

		if ("DAILY".equals(payType) || "HOURLY".equals(payType)) {

			if (unitsWorked == null || unitsWorked.compareTo(BigDecimal.ZERO) <= 0) {

				throw new BusinessRuleException("Units worked is required for " + payType + " salary.");
			}

			return rate.multiply(unitsWorked).setScale(4, RoundingMode.HALF_UP);
		}

		throw new BusinessRuleException("Unsupported salary pay type: " + payType);
	}

	public StaffMonthlyCostResponse post(Long id) {

		StaffMonthlyCostEntity cost = getForUpdate(id);

		ensureDraft(cost);

		Map<String, Object> before = snapshot(cost);

		UserEntity user = getCurrentUser();

		cost.setStatus(getStatus("POSTED"));

		cost.setPostedAt(OffsetDateTime.now());

		cost.setPostedByUser(user);

		costRepository.save(cost);

		auditService.log(AuditLogRequest.builder().actionCode("STATUS_CHANGE").moduleCode("PAYROLL")
				.entityType("STAFF_MONTHLY_COST").entityId(cost.getId()).description("Monthly staff cost posted.")
				.beforeData(before).afterData(snapshot(cost)).build());

		return payrollMapper.toResponse(cost);
	}

	public StaffMonthlyCostResponse voidCost(Long id, VoidStaffMonthlyCostRequest request) {

		StaffMonthlyCostEntity cost = getForUpdate(id);

		if (!"POSTED".equalsIgnoreCase(cost.getStatus().getCode())) {

			throw new BusinessRuleException("Only posted payroll records can be voided.");
		}

		Map<String, Object> before = snapshot(cost);

		cost.setStatus(getStatus("VOIDED"));

		cost.setVoidedAt(OffsetDateTime.now());

		cost.setVoidedByUser(getCurrentUser());

		cost.setVoidReason(request.getReason().trim());

		/*
		 * Allows a corrected replacement record for the same staff/month.
		 */
		cost.setCurrentRecord(false);

		costRepository.save(cost);

		auditService.log(AuditLogRequest.builder().actionCode("STATUS_CHANGE").moduleCode("PAYROLL")
				.entityType("STAFF_MONTHLY_COST").entityId(cost.getId())
				.description("Posted monthly staff cost voided.").beforeData(before).afterData(snapshot(cost)).build());

		return payrollMapper.toResponse(cost);
	}

	private StaffMonthlyCostEntity getForUpdate(Long id) {

		return costRepository.findByIdForUpdate(id)
				.orElseThrow(() -> new ResourceNotFoundException("Payroll record not found."));
	}

	private void ensureDraft(StaffMonthlyCostEntity cost) {

		if (!"DRAFT".equalsIgnoreCase(cost.getStatus().getCode())) {

			throw new BusinessRuleException("Only draft payroll records can be modified.");
		}
	}

	private StaffMonthlyCostStatusEntity getStatus(String code) {

		return statusRepository.findByCodeIgnoreCase(code)
				.orElseThrow(() -> new ResourceNotFoundException("Payroll status not found: " + code));
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

	private Map<String, Object> snapshot(StaffMonthlyCostEntity cost) {

		Map<String, Object> data = new LinkedHashMap<>();

		data.put("costNumber", cost.getCostNumber());

		data.put("staffId", cost.getStaff().getId());

		data.put("periodYear", cost.getPeriodYear());

		data.put("periodMonth", cost.getPeriodMonth());

		data.put("status", cost.getStatus().getCode());

		data.put("payType", cost.getPayTypeCodeSnapshot());

		data.put("baseRate", cost.getBaseRateSnapshot());

		data.put("basePay", cost.getBasePay());

		data.put("grossPay", cost.getGrossPay());

		data.put("netPay", cost.getNetPay());

		data.put("totalStaffCost", cost.getTotalStaffCost());

		data.put("currentRecord", cost.getCurrentRecord());

		data.put("postedAt", cost.getPostedAt());

		data.put("voidedAt", cost.getVoidedAt());

		data.put("voidReason", cost.getVoidReason());

		return data;
	}
}