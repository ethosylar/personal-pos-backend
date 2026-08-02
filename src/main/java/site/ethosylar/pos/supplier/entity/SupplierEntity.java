package site.ethosylar.pos.supplier.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.ethosylar.pos.common.entity.TimestampedEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dt_suppliers_sup")
public class SupplierEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code", nullable = false, length = 50, unique = true)
	private String code;

	@Column(name = "name", nullable = false, length = 150)
	private String name;

	@Column(name = "contact_person", length = 150)
	private String contactPerson;

	@Column(name = "phone", length = 50)
	private String phone;

	@Column(name = "email", length = 150)
	private String email;

	@Column(name = "address_line_1", length = 255)
	private String addressLine1;

	@Column(name = "address_line_2", length = 255)
	private String addressLine2;

	@Column(name = "city", length = 100)
	private String city;

	@Column(name = "state", length = 100)
	private String state;

	@Column(name = "postcode", length = 30)
	private String postcode;

	@Column(name = "country", length = 100)
	private String country;

	@Column(name = "registration_no", length = 100)
	private String registrationNo;

	@Column(name = "tax_registration_no", length = 100)
	private String taxRegistrationNo;

	@Column(name = "payment_terms_days", nullable = false)
	private Integer paymentTermsDays = 0;

	@Column(name = "notes", length = 1000)
	private String notes;

	@Column(name = "is_active", nullable = false)
	private Boolean active = true;
}