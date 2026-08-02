package site.ethosylar.pos.product.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductRequest {

	@NotBlank
	@Size(max = 150)
	private String name;

	@NotNull
	@Positive
	private Long productCategoryId;

	@Size(max = 100)
	private String sku;

	@Size(max = 100)
	private String barcode;

	@Size(max = 1000)
	private String description;

	@Min(0)
	private Integer displayOrder = 0;
}