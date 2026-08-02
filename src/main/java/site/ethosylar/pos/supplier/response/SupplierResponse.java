package site.ethosylar.pos.supplier.response;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SupplierResponse {

	private Long id;

	private String code;

	private String name;

	private String contactPerson;

	private String phone;

	private String email;

	private String addressLine1;

	private String addressLine2;

	private String city;

	private String state;

	private String postcode;

	private String country;

	private String registrationNo;

	private String taxRegistrationNo;

	private Integer paymentTermsDays;

	private String notes;

	private Boolean active;

	private OffsetDateTime createdAt;

	private OffsetDateTime updatedAt;
}