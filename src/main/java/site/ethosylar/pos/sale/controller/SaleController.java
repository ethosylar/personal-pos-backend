package site.ethosylar.pos.sale.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.response.PageResponse;
import site.ethosylar.pos.sale.request.SaleFilterRequest;
import site.ethosylar.pos.sale.request.VoidSaleRequest;
import site.ethosylar.pos.sale.response.SaleResponse;
import site.ethosylar.pos.sale.response.SaleSummaryResponse;
import site.ethosylar.pos.sale.service.SaleQueryService;
import site.ethosylar.pos.sale.service.SaleService;
import site.ethosylar.pos.sale.service.SaleVoidService;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

	private final SaleService saleService;

	private final SaleVoidService saleVoidService;
	
	private final SaleQueryService saleQueryService;

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

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'SALES', 'VIEW')")
	@GetMapping
	public ResponseEntity<PageResponse<SaleSummaryResponse>> search(@Valid @ModelAttribute SaleFilterRequest filter) {

		return ResponseEntity.ok(saleQueryService.search(filter));
	}
}