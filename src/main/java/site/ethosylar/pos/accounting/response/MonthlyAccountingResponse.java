package site.ethosylar.pos.accounting.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthlyAccountingResponse {

	private Long id;

	private String accountingNumber;

	private Integer periodYear;

	private Integer periodMonth;

	private Long statusId;

	private String statusCode;

	private String statusName;

	// SALES

	private Integer salesCount;

	private BigDecimal netSalesRevenue;

	private BigDecimal taxCollected;

	private BigDecimal cogs;

	private BigDecimal grossProfit;

	// EXPENSES

	private Integer postedExpenseCount;

	private BigDecimal operatingExpenses;

	// PAYROLL

	private Integer postedPayrollCount;

	private BigDecimal staffCost;

	// RESULT

	private BigDecimal operatingProfit;

	// CONTROL

	private String notes;

	private OffsetDateTime calculatedAt;

	private Long calculatedByUserId;

	private String calculatedByUsername;

	private OffsetDateTime postedAt;

	private Long postedByUserId;

	private String postedByUsername;

	private OffsetDateTime voidedAt;

	private Long voidedByUserId;

	private String voidedByUsername;

	private String voidReason;

	private Boolean currentRecord;

	private OffsetDateTime createdAt;

	private OffsetDateTime updatedAt;
}