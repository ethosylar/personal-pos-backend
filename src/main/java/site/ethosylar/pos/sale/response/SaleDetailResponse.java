package site.ethosylar.pos.sale.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import site.ethosylar.pos.payment.response.PaymentResponse;

@Getter
@Builder
public class SaleDetailResponse {

	private Long id;

	private String saleNumber;

	private String statusCode;

	private String statusName;

	private OffsetDateTime soldAt;

	private BigDecimal subtotal;

	private BigDecimal discountAmount;

	private BigDecimal taxAmount;

	private BigDecimal totalAmount;

	private BigDecimal totalCost;

	private BigDecimal grossProfit;

	private OffsetDateTime voidedAt;

	private Long voidedByUserId;

	private String voidReason;

	private List<SaleItemResponse> items;

	private List<PaymentResponse> payments;
}