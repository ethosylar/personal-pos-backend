package site.ethosylar.pos.expense.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoidExpenseRequest {

	@NotBlank
	@Size(max = 500)
	private String reason;
}