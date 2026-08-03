package site.ethosylar.pos.expense.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import site.ethosylar.pos.supplier.entity.SupplierEntity;
import site.ethosylar.pos.user.entity.UserEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dt_expenses_exp")
public class ExpenseEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "expense_number", nullable = false, length = 100, unique = true)
	private String expenseNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private ExpenseCategoryEntity category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id", nullable = false)
	private ExpenseStatusEntity status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supplier_id")
	private SupplierEntity supplier;

	@Column(name = "expense_date", nullable = false)
	private LocalDate expenseDate;

	@Column(name = "description", nullable = false, length = 500)
	private String description;

	@Column(name = "amount", nullable = false, precision = 18, scale = 4)
	private BigDecimal amount;

	@Column(name = "tax_amount", nullable = false, precision = 18, scale = 4)
	private BigDecimal taxAmount = BigDecimal.ZERO;

	@Column(name = "total_amount", nullable = false, precision = 18, scale = 4)
	private BigDecimal totalAmount;

	@Column(name = "payee_name", length = 150)
	private String payeeName;

	@Column(name = "reference_no", length = 150)
	private String referenceNo;

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
}