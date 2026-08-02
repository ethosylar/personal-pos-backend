package site.ethosylar.pos.ingredient.entity;

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
import site.ethosylar.pos.supplier.entity.SupplierEntity;
import site.ethosylar.pos.unit.entity.UnitOfMeasureEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dt_ingredient_suppliers_is")
public class IngredientSupplierEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ingredient_id", nullable = false)
	private IngredientEntity ingredient;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supplier_id", nullable = false)
	private SupplierEntity supplier;

	@Column(name = "supplier_item_code", length = 100)
	private String supplierItemCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "purchase_unit_id", nullable = false)
	private UnitOfMeasureEntity purchaseUnit;

	@Column(name = "base_quantity", nullable = false, precision = 18, scale = 4)
	private BigDecimal baseQuantity;

	@Column(name = "purchase_price", nullable = false, precision = 18, scale = 4)
	private BigDecimal purchasePrice;

	@Column(name = "is_preferred", nullable = false)
	private Boolean preferred = false;

	@Column(name = "is_active", nullable = false)
	private Boolean active = true;

	@Column(name = "notes", length = 1000)
	private String notes;
}