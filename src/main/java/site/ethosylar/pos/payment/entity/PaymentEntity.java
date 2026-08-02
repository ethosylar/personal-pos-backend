package site.ethosylar.pos.payment.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.ethosylar.pos.common.entity.TimestampedEntity;
import site.ethosylar.pos.sale.entity.SaleEntity;
import site.ethosylar.pos.user.entity.UserEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dt_payments_pay")
public class PaymentEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sale_id", nullable = false)
	private SaleEntity sale;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_method_id", nullable = false)
	private PaymentMethodEntity paymentMethod;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_status_id", nullable = false)
	private PaymentStatusEntity paymentStatus;

	@Column(name = "amount", nullable = false, precision = 18, scale = 4)
	private BigDecimal amount;

	@Column(name = "tendered_amount", precision = 18, scale = 4)
	private BigDecimal tenderedAmount;

	@Column(name = "change_amount", precision = 18, scale = 4)
	private BigDecimal changeAmount;

	@Column(name = "reference_no", length = 150)
	private String referenceNo;

	@Column(name = "paid_at")
	private OffsetDateTime paidAt;

	@Column(name = "notes", length = 1000)
	private String notes;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by_user_id")
	private UserEntity createdByUser;
}