package site.ethosylar.pos.product.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductFilterRequest {

	private String search;

	@Positive
	private Long categoryId;

	private Boolean active;

	private Boolean available;

	@Min(1)
	private Integer page = 1;

	@Min(1)
	@Max(100)
	private Integer perPage = 20;
}