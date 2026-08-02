package site.ethosylar.pos.procurement.request;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGoodsReceiptRequest {

	private OffsetDateTime receivedAt;

	@Size(max = 100)
	private String supplierDeliveryNo;

	@Size(max = 1000)
	private String notes;

	@Valid
	@NotEmpty
	private List<CreateGoodsReceiptItemRequest> items = new ArrayList<>();
}