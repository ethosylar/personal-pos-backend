package site.ethosylar.pos.supplier.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SupplierSummaryResponse {

	private Long id;

	private String code;

	private String name;

	private String contactPerson;

	private String phone;

	private String email;

	private Boolean active;
}