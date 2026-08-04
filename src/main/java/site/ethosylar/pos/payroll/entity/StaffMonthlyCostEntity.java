package site.ethosylar.pos.payroll.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.ethosylar.pos.common.entity.TimestampedEntity;
import site.ethosylar.pos.staff.entity.StaffEntity;
import site.ethosylar.pos.staff.entity.StaffSalaryRateEntity;
import site.ethosylar.pos.user.entity.UserEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dt_staff_monthly_costs_smc")
public class StaffMonthlyCostEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "cost_number", nullable = false, unique = true, length = 100)
	private String costNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "staff_id", nullable = false)
	private StaffEntity staff;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "salary_rate_id", nullable = false)
	private StaffSalaryRateEntity salaryRate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id", nullable = false)
	private StaffMonthlyCostStatusEntity status;

	@Column(name = "period_year", nullable = false)
	private Short periodYear;

	@Column(name = "period_month", nullable = false)
	private Short periodMonth;

	@Column(name = "pay_type_code_snapshot", nullable = false, length = 50)
	private String payTypeCodeSnapshot;

	@Column(name = "base_rate_snapshot", nullable = false, precision = 18, scale = 4)
	private BigDecimal baseRateSnapshot;

	@Column(name = "fixed_allowance_snapshot", nullable = false, precision = 18, scale = 4)
	private BigDecimal fixedAllowanceSnapshot = BigDecimal.ZERO;

	@Column(name = "units_worked", precision = 18, scale = 4)
	private BigDecimal unitsWorked;

	@Column(name = "base_pay", nullable = false, precision = 18, scale = 4)
	private BigDecimal basePay;

	@Column(name = "additional_allowance", nullable = false, precision = 18, scale = 4)
	private BigDecimal additionalAllowance = BigDecimal.ZERO;

	@Column(name = "overtime_pay", nullable = false, precision = 18, scale = 4)
	private BigDecimal overtimePay = BigDecimal.ZERO;

	@Column(name = "bonus_amount", nullable = false, precision = 18, scale = 4)
	private BigDecimal bonusAmount = BigDecimal.ZERO;

	@Column(name = "deduction_amount", nullable = false, precision = 18, scale = 4)
	private BigDecimal deductionAmount = BigDecimal.ZERO;

	@Column(name = "employer_contribution", nullable = false, precision = 18, scale = 4)
	private BigDecimal employerContribution = BigDecimal.ZERO;

	@Column(name = "gross_pay", nullable = false, precision = 18, scale = 4)
	private BigDecimal grossPay;

	@Column(name = "net_pay", nullable = false, precision = 18, scale = 4)
	private BigDecimal netPay;

	@Column(name = "total_staff_cost", nullable = false, precision = 18, scale = 4)
	private BigDecimal totalStaffCost;

	@Column(name = "notes", length = 1000)
	private String notes;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by_user_id")
	private UserEntity createdByUser;

	@Column(name = "posted_at")
	private OffsetDateTime postedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "posted_by_user_id")
	private UserEntity postedByUser;

	@Column(name = "voided_at")
	private OffsetDateTime voidedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "voided_by_user_id")
	private UserEntity voidedByUser;

	@Column(name = "void_reason", length = 500)
	private String voidReason;

	@Column(name = "is_current_record", nullable = false)
	private Boolean currentRecord = true;
}