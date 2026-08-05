package site.ethosylar.pos.accounting.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoidMonthlyAccountingRequest {

	@NotBlank
	@Size(max = 500)
	private String reason;
}