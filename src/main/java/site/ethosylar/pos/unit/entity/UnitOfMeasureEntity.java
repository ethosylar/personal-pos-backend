package site.ethosylar.pos.unit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.ethosylar.pos.common.entity.TimestampedEntity;
import site.ethosylar.pos.unit.constant.MeasurementType;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lt_units_uom")
public class UnitOfMeasureEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code", nullable = false, length = 30, unique = true)
	private String code;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "symbol", length = 20)
	private String symbol;

	@Enumerated(EnumType.STRING)
	@Column(name = "measurement_type", nullable = false, length = 30)
	private MeasurementType measurementType;

	@Column(name = "is_base_unit", nullable = false)
	private Boolean baseUnit = false;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder = 0;

	@Column(name = "is_active", nullable = false)
	private Boolean active = true;
}