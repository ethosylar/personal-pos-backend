package site.ethosylar.pos.product.entity;

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
import site.ethosylar.pos.category.entity.ProductCategoryEntity;
import site.ethosylar.pos.common.entity.TimestampedEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dt_products_prd")
public class ProductEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code", nullable = false, length = 50, unique = true)
	private String code;

	@Column(name = "name", nullable = false, length = 150)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_category_id", nullable = false)
	private ProductCategoryEntity category;

	@Column(name = "sku", length = 100, unique = true)
	private String sku;

	@Column(name = "barcode", length = 100, unique = true)
	private String barcode;

	@Column(name = "description", length = 1000)
	private String description;

	@Column(name = "selling_price", nullable = false, precision = 18, scale = 4)
	private BigDecimal sellingPrice = BigDecimal.ZERO;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder = 0;

	@Column(name = "is_available", nullable = false)
	private Boolean available = true;

	@Column(name = "is_active", nullable = false)
	private Boolean active = true;
}