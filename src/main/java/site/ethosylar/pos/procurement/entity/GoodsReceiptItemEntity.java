package site.ethosylar.pos.procurement.entity;

import java.math.BigDecimal;

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

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dt_goods_receipt_items_gri")
public class GoodsReceiptItemEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "goods_receipt_id", nullable = false)
	private GoodsReceiptEntity goodsReceipt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "purchase_order_item_id", nullable = false)
	private PurchaseOrderItemEntity purchaseOrderItem;

	@Column(name = "received_quantity", nullable = false, precision = 18, scale = 4)
	private BigDecimal receivedQuantity;

	@Column(name = "base_quantity_per_unit", nullable = false, precision = 18, scale = 4)
	private BigDecimal baseQuantityPerUnit;

	@Column(name = "unit_price", nullable = false, precision = 18, scale = 4)
	private BigDecimal unitPrice;

	@Column(name = "received_base_quantity", nullable = false, precision = 18, scale = 4)
	private BigDecimal receivedBaseQuantity;

	@Column(name = "base_unit_cost", nullable = false, precision = 18, scale = 6)
	private BigDecimal baseUnitCost;

	@Column(name = "notes", length = 1000)
	private String notes;
}