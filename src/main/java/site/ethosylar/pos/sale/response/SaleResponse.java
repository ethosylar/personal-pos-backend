package site.ethosylar.pos.sale.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import site.ethosylar.pos.payment.response.PaymentResponse;

@Getter
@Builder
public class SaleResponse {

	private Long id;

	private String saleNumber;

	private String statusCode;

	private String statusName;

	private Long cashierUserId;

	private String cashierUsername;

	private OffsetDateTime soldAt;

	private BigDecimal subtotal;

	private BigDecimal discountAmount;

	private BigDecimal taxAmount;

	private BigDecimal totalAmount;

	private BigDecimal totalCostSnapshot;

	private BigDecimal grossProfitSnapshot;

	private String notes;

	private List<SaleItemResponse> items;

	private List<PaymentResponse> payments;

	private OffsetDateTime createdAt;

	private OffsetDateTime updatedAt;
}