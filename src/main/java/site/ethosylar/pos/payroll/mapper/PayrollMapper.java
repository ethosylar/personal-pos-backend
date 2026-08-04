package site.ethosylar.pos.payroll.mapper;

import org.springframework.stereotype.Component;

import site.ethosylar.pos.payroll.entity.StaffMonthlyCostEntity;
import site.ethosylar.pos.payroll.response.StaffMonthlyCostResponse;

@Component
public class PayrollMapper {

	public StaffMonthlyCostResponse toResponse(StaffMonthlyCostEntity cost) {

		return StaffMonthlyCostResponse.builder()

				.id(cost.getId())

				.costNumber(cost.getCostNumber())

				.staffId(cost.getStaff().getId())

				.staffCode(cost.getStaff().getStaffCode())

				.staffName(cost.getStaff().getFullName())

				.salaryRateId(cost.getSalaryRate().getId())

				.statusId(cost.getStatus().getId())

				.statusCode(cost.getStatus().getCode())

				.statusName(cost.getStatus().getName())

				.periodYear(cost.getPeriodYear().intValue())

				.periodMonth(cost.getPeriodMonth().intValue())

				.payTypeCode(cost.getPayTypeCodeSnapshot())

				.baseRateSnapshot(cost.getBaseRateSnapshot())

				.fixedAllowanceSnapshot(cost.getFixedAllowanceSnapshot())

				.unitsWorked(cost.getUnitsWorked())

				.basePay(cost.getBasePay())

				.additionalAllowance(cost.getAdditionalAllowance())

				.overtimePay(cost.getOvertimePay())

				.bonusAmount(cost.getBonusAmount())

				.deductionAmount(cost.getDeductionAmount())

				.employerContribution(cost.getEmployerContribution())

				.grossPay(cost.getGrossPay())

				.netPay(cost.getNetPay())

				.totalStaffCost(cost.getTotalStaffCost())

				.notes(cost.getNotes())

				.createdByUserId(cost.getCreatedByUser() != null ? cost.getCreatedByUser().getId() : null)

				.createdByUsername(cost.getCreatedByUser() != null ? cost.getCreatedByUser().getUsername() : null)

				.postedAt(cost.getPostedAt())

				.postedByUserId(cost.getPostedByUser() != null ? cost.getPostedByUser().getId() : null)

				.postedByUsername(cost.getPostedByUser() != null ? cost.getPostedByUser().getUsername() : null)

				.voidedAt(cost.getVoidedAt())

				.voidedByUserId(cost.getVoidedByUser() != null ? cost.getVoidedByUser().getId() : null)

				.voidedByUsername(cost.getVoidedByUser() != null ? cost.getVoidedByUser().getUsername() : null)

				.voidReason(cost.getVoidReason())

				.currentRecord(cost.getCurrentRecord())

				.createdAt(cost.getCreatedAt())

				.updatedAt(cost.getUpdatedAt())

				.build();
	}
}