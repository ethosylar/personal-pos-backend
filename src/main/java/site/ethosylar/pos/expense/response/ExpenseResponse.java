package site.ethosylar.pos.expense.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExpenseResponse {

	private Long id;

	private String expenseNumber;

	private Long categoryId;

	private String categoryCode;

	private String categoryName;

	private String expenseClass;

	private Long statusId;

	private String statusCode;

	private String statusName;

	private Long supplierId;

	private String supplierCode;

	private String supplierName;

	private LocalDate expenseDate;

	private String description;

	private BigDecimal amount;

	private BigDecimal taxAmount;

	private BigDecimal totalAmount;

	private String payeeName;

	private String referenceNo;

	private String notes;

	private Long createdByUserId;

	private String createdByUsername;

	private OffsetDateTime postedAt;

	private Long postedByUserId;

	private String postedByUsername;

	private OffsetDateTime voidedAt;

	private Long voidedByUserId;

	private String voidedByUsername;

	private String voidReason;

	private OffsetDateTime createdAt;

	private OffsetDateTime updatedAt;
}