package site.ethosylar.pos.payment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.ethosylar.pos.common.entity.TimestampedEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lt_payment_methods_pm")
public class PaymentMethodEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code", nullable = false, length = 50, unique = true)
	private String code;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "description", length = 500)
	private String description;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder = 0;

	@Column(name = "is_active", nullable = false)
	private Boolean active = true;
}