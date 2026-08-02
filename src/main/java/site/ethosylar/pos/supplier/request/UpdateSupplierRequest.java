package site.ethosylar.pos.supplier.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSupplierRequest {

	@NotBlank
	@Size(max = 150)
	private String name;

	@Size(max = 150)
	private String contactPerson;

	@Size(max = 50)
	private String phone;

	@Email
	@Size(max = 150)
	private String email;

	@Size(max = 255)
	private String addressLine1;

	@Size(max = 255)
	private String addressLine2;

	@Size(max = 100)
	private String city;

	@Size(max = 100)
	private String state;

	@Size(max = 30)
	private String postcode;

	@Size(max = 100)
	private String country;

	@Size(max = 100)
	private String registrationNo;

	@Size(max = 100)
	private String taxRegistrationNo;

	@Min(0)
	private Integer paymentTermsDays = 0;

	@Size(max = 1000)
	private String notes;
}