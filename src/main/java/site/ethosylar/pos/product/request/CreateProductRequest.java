package site.ethosylar.pos.product.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductRequest {

	@NotBlank
	@Size(max = 50)
	@Pattern(regexp = "^[A-Z0-9_-]+$", message = "Code may only contain uppercase letters, numbers, underscores and hyphens.")
	private String code;

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

	@NotNull
	@DecimalMin("0.0000")
	private BigDecimal sellingPrice;

	@Min(0)
	private Integer displayOrder = 0;
}