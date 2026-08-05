package site.ethosylar.pos.accounting.entity;

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
import site.ethosylar.pos.user.entity.UserEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dt_monthly_accounting_ma")
public class MonthlyAccountingEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "accounting_number", nullable = false, unique = true, length = 100)
	private String accountingNumber;

	/*
	 * PostgreSQL SMALLINT -> Java Short.
	 */
	@Column(name = "period_year", nullable = false)
	private Short periodYear;

	@Column(name = "period_month", nullable = false)
	private Short periodMonth;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id", nullable = false)
	private MonthlyAccountingStatusEntity status;

	// =========================================================
	// SALES
	// =========================================================

	@Column(name = "sales_count", nullable = false)
	private Integer salesCount = 0;

	@Column(name = "net_sales_revenue", nullable = false, precision = 18, scale = 4)
	private BigDecimal netSalesRevenue = BigDecimal.ZERO;

	@Column(name = "tax_collected", nullable = false, precision = 18, scale = 4)
	private BigDecimal taxCollected = BigDecimal.ZERO;

	@Column(name = "cogs", nullable = false, precision = 18, scale = 6)
	private BigDecimal cogs = BigDecimal.ZERO;

	@Column(name = "gross_profit", nullable = false, precision = 18, scale = 6)
	private BigDecimal grossProfit = BigDecimal.ZERO;

	// =========================================================
	// EXPENSES
	// =========================================================

	@Column(name = "posted_expense_count", nullable = false)
	private Integer postedExpenseCount = 0;

	@Column(name = "operating_expenses", nullable = false, precision = 18, scale = 4)
	private BigDecimal operatingExpenses = BigDecimal.ZERO;

	// =========================================================
	// PAYROLL
	// =========================================================

	@Column(name = "posted_payroll_count", nullable = false)
	private Integer postedPayrollCount = 0;

	@Column(name = "staff_cost", nullable = false, precision = 18, scale = 4)
	private BigDecimal staffCost = BigDecimal.ZERO;

	// =========================================================
	// RESULT
	// =========================================================

	@Column(name = "operating_profit", nullable = false, precision = 18, scale = 6)
	private BigDecimal operatingProfit = BigDecimal.ZERO;

	// =========================================================
	// CONTROL
	// =========================================================

	@Column(name = "notes", length = 1000)
	private String notes;

	@Column(name = "calculated_at", nullable = false)
	private OffsetDateTime calculatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calculated_by_user_id")
	private UserEntity calculatedByUser;

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