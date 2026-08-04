package site.ethosylar.pos.staff.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SalaryRateResponse {

	private Long id;

	private Long staffId;

	private Long payTypeId;

	private String payTypeCode;

	private String payTypeName;

	private BigDecimal baseRate;

	private BigDecimal fixedAllowance;

	private String currencyCode;

	private LocalDate effectiveFrom;

	private LocalDate effectiveTo;

	private Boolean current;

	private String notes;

	private Long createdByUserId;

	private String createdByUsername;

	private OffsetDateTime createdAt;
}