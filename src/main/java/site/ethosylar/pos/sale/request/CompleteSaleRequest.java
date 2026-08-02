package site.ethosylar.pos.sale.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import site.ethosylar.pos.payment.request.CheckoutPaymentRequest;

@Getter
@Setter
public class CompleteSaleRequest {

	@Valid
	@NotEmpty
	private List<CheckoutPaymentRequest> payments = new ArrayList<>();
}