package site.ethosylar.pos.procurement.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

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
@Table(name = "dt_purchase_orders_po")
public class PurchaseOrderEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "po_number", nullable = false, length = 100, unique = true)
	private String poNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supplier_id", nullable = false)
	private SupplierEntity supplier;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id", nullable = false)
	private PurchaseOrderStatusEntity status;

	@Column(name = "order_date", nullable = false)
	private LocalDate orderDate;

	@Column(name = "expected_delivery_date")
	private LocalDate expectedDeliveryDate;

	@Column(name = "subtotal", nullable = false, precision = 18, scale = 4)
	private BigDecimal subtotal = BigDecimal.ZERO;

	@Column(name = "tax_amount", nullable = false, precision = 18, scale = 4)
	private BigDecimal taxAmount = BigDecimal.ZERO;

	@Column(name = "delivery_fee", nullable = false, precision = 18, scale = 4)
	private BigDecimal deliveryFee = BigDecimal.ZERO;

	@Column(name = "total_amount", nullable = false, precision = 18, scale = 4)
	private BigDecimal totalAmount = BigDecimal.ZERO;

	@Column(name = "notes", length = 1000)
	private String notes;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by_user_id")
	private UserEntity createdByUser;
}