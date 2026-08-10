package site.ethosylar.pos.sale.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleFilterRequest {

	private String search;

	private String statusCode;

	@Positive
	private Long cashierUserId;

//	private OffsetDateTime fromDate;
//
//	private OffsetDateTime toDate;
	
	 private String status;
	 
	private LocalDate fromDate;

	private LocalDate toDate;

	@Min(1)
	private Integer page = 1;

	@Min(1)
	@Max(100)
	private Integer perPage = 20;
}