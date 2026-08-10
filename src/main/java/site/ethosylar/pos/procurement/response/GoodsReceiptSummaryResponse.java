package site.ethosylar.pos.procurement.response;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoodsReceiptSummaryResponse {

	private Long id;

	private String receiptNumber;

	private LocalDate receiptDate;

	private Integer itemCount;

	private OffsetDateTime createdAt;
}
