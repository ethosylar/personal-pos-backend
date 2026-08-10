package site.ethosylar.pos.sale.controller;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.config.ApiPaths;
import site.ethosylar.pos.common.response.PageResponse;
import site.ethosylar.pos.sale.request.SaleFilterRequest;
import site.ethosylar.pos.sale.request.VoidSaleRequest;
import site.ethosylar.pos.sale.response.SaleResponse;
import site.ethosylar.pos.sale.response.SaleSummaryResponse;
import site.ethosylar.pos.sale.service.SaleQueryService;
import site.ethosylar.pos.sale.service.SaleService;
import site.ethosylar.pos.sale.service.SaleVoidService;

@RestController
@RequestMapping(ApiPaths.V1 + "/sales")
@RequiredArgsConstructor
public class SaleController {

	private final SaleService saleService;

	private final SaleVoidService saleVoidService;

	private final SaleQueryService saleQueryService;

	private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Kuala_Lumpur");

	// =========================================================
	// GET SALE DETAIL / HISTORY
	// =========================================================

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'SALES', 'VIEW')")
	@GetMapping("/{saleId}")
	public ResponseEntity<SaleResponse> getById(@PathVariable Long saleId) {

		return ResponseEntity.ok(saleService.getById(saleId));
	}

	// =========================================================
	// VOID COMPLETED SALE
	// =========================================================

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'SALES', 'VOID_SALE')")
	@PostMapping("/{saleId}/void")
	public ResponseEntity<SaleResponse> voidSale(@PathVariable Long saleId,

			@Valid @RequestBody VoidSaleRequest request) {

		return ResponseEntity.ok(saleVoidService.voidSale(saleId, request));
	}

	@GetMapping
	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'SALES', 'VIEW')")
	public ResponseEntity<PageResponse<SaleSummaryResponse>> search(@ModelAttribute SaleFilterRequest request) {

		return ResponseEntity.ok(saleQueryService.getSales(request.getSearch(), request.getStatus(),
				request.getFromDate(), request.getToDate(), request.getPage() != null ? request.getPage() : 1,
				request.getPerPage() != null ? request.getPerPage() : 20));
	}

	@GetMapping
	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'SALES', 'VIEW')")
	public ResponseEntity<PageResponse<SaleSummaryResponse>> index(

			@RequestParam(required = false) String search,

			@RequestParam(required = false) String status,

			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,

			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,

			@RequestParam(defaultValue = "1") int page,

			@RequestParam(defaultValue = "20") int perPage) {

		return ResponseEntity.ok(saleQueryService.getSales(search, status, fromDate, toDate, page, perPage));
	}
}