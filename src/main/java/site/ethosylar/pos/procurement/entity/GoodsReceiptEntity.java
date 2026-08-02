package site.ethosylar.pos.procurement.entity;

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
@Table(name = "dt_goods_receipts_gr")
public class GoodsReceiptEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "receipt_number", nullable = false, length = 100, unique = true)
	private String receiptNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "purchase_order_id", nullable = false)
	private PurchaseOrderEntity purchaseOrder;

	@Column(name = "received_at", nullable = false)
	private OffsetDateTime receivedAt;

	@Column(name = "supplier_delivery_no", length = 100)
	private String supplierDeliveryNo;

	@Column(name = "notes", length = 1000)
	private String notes;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "received_by_user_id")
	private UserEntity receivedByUser;
}