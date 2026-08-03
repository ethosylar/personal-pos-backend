package site.ethosylar.pos.sale.entity;

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
@Table(name = "dt_sales_sal")
public class SaleEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "sale_number", nullable = false, length = 100, unique = true)
	private String saleNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id", nullable = false)
	private SaleStatusEntity status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cashier_user_id")
	private UserEntity cashierUser;

	@Column(name = "sold_at")
	private OffsetDateTime soldAt;

	@Column(name = "subtotal", nullable = false, precision = 18, scale = 4)
	private BigDecimal subtotal = BigDecimal.ZERO;

	@Column(name = "discount_amount", nullable = false, precision = 18, scale = 4)
	private BigDecimal discountAmount = BigDecimal.ZERO;

	@Column(name = "tax_amount", nullable = false, precision = 18, scale = 4)
	private BigDecimal taxAmount = BigDecimal.ZERO;

	@Column(name = "total_amount", nullable = false, precision = 18, scale = 4)
	private BigDecimal totalAmount = BigDecimal.ZERO;

	@Column(name = "total_cost_snapshot", precision = 18, scale = 6)
	private BigDecimal totalCostSnapshot;

	@Column(name = "gross_profit_snapshot", precision = 18, scale = 6)
	private BigDecimal grossProfitSnapshot;

	@Column(name = "notes", length = 1000)
	private String notes;

	@Column(name = "voided_at")
	private OffsetDateTime voidedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "voided_by_user_id")
	private UserEntity voidedByUser;

	@Column(name = "void_reason", length = 500)
	private String voidReason;
}