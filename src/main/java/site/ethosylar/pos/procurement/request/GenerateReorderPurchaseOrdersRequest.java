package site.ethosylar.pos.procurement.request;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateReorderPurchaseOrdersRequest {

	/*
	 * User-selected ingredients from the reorder recommendation screen.
	 */
	@NotEmpty
	private List<@Positive Long> ingredientIds = new ArrayList<>();

	private LocalDate expectedDeliveryDate;

	@Size(max = 1000)
	private String notes;
}