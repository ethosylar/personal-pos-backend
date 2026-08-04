package site.ethosylar.pos.staff.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStaffRequest {

	@NotBlank
	@Size(max = 50)
	private String staffCode;

	@NotBlank
	@Size(max = 150)
	private String fullName;

	@Email
	@Size(max = 150)
	private String email;

	@Size(max = 50)
	private String phone;

	@Positive
	private Long userId;

	@NotNull
	@Positive
	private Long statusId;

	@NotNull
	private LocalDate hireDate;

	private LocalDate terminationDate;

	@Size(max = 1000)
	private String notes;
}