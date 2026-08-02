package site.ethosylar.pos.product.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeProductAvailabilityRequest {

	@NotNull
	private Boolean available;
}