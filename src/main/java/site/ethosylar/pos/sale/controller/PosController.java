package site.ethosylar.pos.sale.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.config.ApiPaths;
import site.ethosylar.pos.sale.request.CompleteSaleRequest;
import site.ethosylar.pos.sale.request.CreateSaleItemRequest;
import site.ethosylar.pos.sale.request.CreateSaleRequest;
import site.ethosylar.pos.sale.request.UpdateSaleItemRequest;
import site.ethosylar.pos.sale.request.UpdateSaleRequest;
import site.ethosylar.pos.sale.response.SaleResponse;
import site.ethosylar.pos.sale.service.SaleCheckoutService;
import site.ethosylar.pos.sale.service.SaleService;

@RestController
@RequestMapping(ApiPaths.V1 + "/pos/sales")
@RequiredArgsConstructor
public class PosController {

	private final SaleService saleService;

	private final SaleCheckoutService saleCheckoutService;

	// =========================================================
	// CREATE DRAFT SALE
	// =========================================================

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'POS', 'CREATE_SALE')")
	@PostMapping
	public ResponseEntity<SaleResponse> createSale(@Valid @RequestBody CreateSaleRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(saleService.create(request));
	}

	// =========================================================
	// ADD PRODUCT TO DRAFT SALE
	// =========================================================

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'POS', 'CREATE_SALE')")
	@PostMapping("/{saleId}/items")
	public ResponseEntity<SaleResponse> addItem(@PathVariable Long saleId,

			@Valid @RequestBody CreateSaleItemRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(saleService.addItem(saleId, request));
	}

	// =========================================================
	// COMPLETE CHECKOUT
	// =========================================================

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'POS', 'COMPLETE_SALE')")
	@PostMapping("/{saleId}/complete")
	public ResponseEntity<SaleResponse> completeSale(@PathVariable Long saleId,

			@Valid @RequestBody CompleteSaleRequest request) {

		return ResponseEntity.ok(saleCheckoutService.complete(saleId, request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'POS', 'CREATE_SALE')")
	@GetMapping("/{saleId}")
	public ResponseEntity<SaleResponse> getSale(@PathVariable Long saleId) {

		return ResponseEntity.ok(saleService.getById(saleId));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'POS', 'CREATE_SALE')")
	@PutMapping("/{saleId}")
	public ResponseEntity<SaleResponse> updateSale(@PathVariable Long saleId,
			@Valid @RequestBody UpdateSaleRequest request) {

		return ResponseEntity.ok(saleService.update(saleId, request));
	}

	@PutMapping("/{saleId}/items/{itemId}")
	public ResponseEntity<SaleResponse> updateItem(@PathVariable Long saleId, @PathVariable Long itemId,
			@Valid @RequestBody UpdateSaleItemRequest request) {

		return ResponseEntity.ok(saleService.updateItem(saleId, itemId, request));
	}

	@DeleteMapping("/{saleId}/items/{itemId}")
	public ResponseEntity<SaleResponse> deleteItem(@PathVariable Long saleId, @PathVariable Long itemId) {

		return ResponseEntity.ok(saleService.deleteItem(saleId, itemId));
	}
}