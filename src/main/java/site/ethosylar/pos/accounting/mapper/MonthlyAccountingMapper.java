package site.ethosylar.pos.accounting.mapper;

import org.springframework.stereotype.Component;

import site.ethosylar.pos.accounting.entity.MonthlyAccountingEntity;
import site.ethosylar.pos.accounting.response.MonthlyAccountingResponse;

@Component
public class MonthlyAccountingMapper {

	public MonthlyAccountingResponse toResponse(MonthlyAccountingEntity accounting) {

		return MonthlyAccountingResponse.builder()

				.id(accounting.getId())

				.accountingNumber(accounting.getAccountingNumber())

				.periodYear(accounting.getPeriodYear().intValue())

				.periodMonth(accounting.getPeriodMonth().intValue())

				.statusId(accounting.getStatus().getId())

				.statusCode(accounting.getStatus().getCode())

				.statusName(accounting.getStatus().getName())

				.salesCount(accounting.getSalesCount())

				.netSalesRevenue(accounting.getNetSalesRevenue())

				.taxCollected(accounting.getTaxCollected())

				.cogs(accounting.getCogs())

				.grossProfit(accounting.getGrossProfit())

				.postedExpenseCount(accounting.getPostedExpenseCount())

				.operatingExpenses(accounting.getOperatingExpenses())

				.postedPayrollCount(accounting.getPostedPayrollCount())

				.staffCost(accounting.getStaffCost())

				.operatingProfit(accounting.getOperatingProfit())

				.notes(accounting.getNotes())

				.calculatedAt(accounting.getCalculatedAt())

				.calculatedByUserId(
						accounting.getCalculatedByUser() != null ? accounting.getCalculatedByUser().getId() : null)

				.calculatedByUsername(
						accounting.getCalculatedByUser() != null ? accounting.getCalculatedByUser().getUsername()
								: null)

				.postedAt(accounting.getPostedAt())

				.postedByUserId(accounting.getPostedByUser() != null ? accounting.getPostedByUser().getId() : null)

				.postedByUsername(
						accounting.getPostedByUser() != null ? accounting.getPostedByUser().getUsername() : null)

				.voidedAt(accounting.getVoidedAt())

				.voidedByUserId(accounting.getVoidedByUser() != null ? accounting.getVoidedByUser().getId() : null)

				.voidedByUsername(
						accounting.getVoidedByUser() != null ? accounting.getVoidedByUser().getUsername() : null)

				.voidReason(accounting.getVoidReason())

				.currentRecord(accounting.getCurrentRecord())

				.createdAt(accounting.getCreatedAt())

				.updatedAt(accounting.getUpdatedAt())

				.build();
	}
}