package site.ethosylar.pos.staff.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffSummaryResponse {

	private Long id;

	private String staffCode;

	private String fullName;

	private String email;

	private String phone;

	private String statusCode;

	private String statusName;

	private LocalDate hireDate;

	private LocalDate terminationDate;

	private Long userId;

	private String username;
}