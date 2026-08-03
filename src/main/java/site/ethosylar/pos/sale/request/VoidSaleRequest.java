package site.ethosylar.pos.sale.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoidSaleRequest {

	@NotBlank
	@Size(max = 500)
	private String reason;
}