package site.ethosylar.pos.staff.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffLookupResponse {

	private Long id;

	private String code;

	private String name;

	private String description;
}