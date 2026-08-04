package site.ethosylar.pos.staff.response;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffResponse {

	private Long id;

	private String staffCode;

	private String fullName;

	private String email;

	private String phone;

	private Long userId;

	private String username;

	private Long statusId;

	private String statusCode;

	private String statusName;

	private LocalDate hireDate;

	private LocalDate terminationDate;

	private String notes;

	private SalaryRateResponse currentSalaryRate;

	private OffsetDateTime createdAt;

	private OffsetDateTime updatedAt;
}