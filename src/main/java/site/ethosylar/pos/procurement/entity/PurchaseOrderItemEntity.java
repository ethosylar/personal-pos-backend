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
import site.ethosylar.pos.ingredient.entity.IngredientEntity;
import site.ethosylar.pos.unit.entity.UnitOfMeasureEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dt_purchase_order_items_poi")
public class PurchaseOrderItemEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "purchase_order_id", nullable = false)
	private PurchaseOrderEntity purchaseOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ingredient_id", nullable = false)
	private IngredientEntity ingredient;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "purchase_unit_id", nullable = false)
	private UnitOfMeasureEntity purchaseUnit;

	@Column(name = "ordered_quantity", nullable = false, precision = 18, scale = 4)
	private BigDecimal orderedQuantity;

	@Column(name = "base_quantity_per_unit", nullable = false, precision = 18, scale = 4)
	private BigDecimal baseQuantityPerUnit;

	@Column(name = "unit_price", nullable = false, precision = 18, scale = 4)
	private BigDecimal unitPrice;

	@Column(name = "line_subtotal", nullable = false, precision = 18, scale = 4)
	private BigDecimal lineSubtotal;

	@Column(name = "supplier_item_code", length = 100)
	private String supplierItemCode;

	@Column(name = "notes", length = 1000)
	private String notes;
}