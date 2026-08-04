package site.ethosylar.pos.payroll.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffMonthlyCostResponse {

    private Long id;

    private String costNumber;

    private Long staffId;

    private String staffCode;

    private String staffName;

    private Long salaryRateId;

    private Long statusId;

    private String statusCode;

    private String statusName;

    private Integer periodYear;

    private Integer periodMonth;

    private String payTypeCode;

    private BigDecimal baseRateSnapshot;

    private BigDecimal fixedAllowanceSnapshot;

    private BigDecimal unitsWorked;

    private BigDecimal basePay;

    private BigDecimal additionalAllowance;

    private BigDecimal overtimePay;

    private BigDecimal bonusAmount;

    private BigDecimal deductionAmount;

    private BigDecimal employerContribution;

    private BigDecimal grossPay;

    private BigDecimal netPay;

    private BigDecimal totalStaffCost;

    private String notes;

    private Long createdByUserId;

    private String createdByUsername;

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