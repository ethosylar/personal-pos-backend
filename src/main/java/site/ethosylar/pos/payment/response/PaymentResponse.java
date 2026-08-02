package site.ethosylar.pos.payment.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {

	private Long id;

	private Long paymentMethodId;

	private String paymentMethodCode;

	private String paymentMethodName;

	private String paymentStatusCode;

	private BigDecimal amount;

	private BigDecimal tenderedAmount;

	private BigDecimal changeAmount;

	private String referenceNo;

	private OffsetDateTime paidAt;
}