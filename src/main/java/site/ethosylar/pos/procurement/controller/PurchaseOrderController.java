package site.ethosylar.pos.procurement.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
import site.ethosylar.pos.procurement.request.CreateGoodsReceiptRequest;
import site.ethosylar.pos.procurement.request.CreatePurchaseOrderRequest;
import site.ethosylar.pos.procurement.response.GoodsReceiptResponse;
import site.ethosylar.pos.procurement.response.PurchaseOrderResponse;
import site.ethosylar.pos.procurement.response.PurchaseOrderSummaryResponse;
import site.ethosylar.pos.procurement.service.GoodsReceiptService;
import site.ethosylar.pos.procurement.service.PurchaseOrderService;

@RestController
@RequestMapping(ApiPaths.V1 + "/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

	private final PurchaseOrderService purchaseOrderService;

	private final GoodsReceiptService goodsReceiptService;

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'PURCHASING', 'CREATE_PO')")
	@PostMapping
	public ResponseEntity<PurchaseOrderResponse> create(@Valid @RequestBody CreatePurchaseOrderRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrderService.createResponse(request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'PURCHASING', 'PLACE_ORDER')")
	@PostMapping("/{id}/place")
	public ResponseEntity<PurchaseOrderResponse> place(@PathVariable Long id) {

		return ResponseEntity.ok(purchaseOrderService.placeOrderResponse(id));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'PURCHASING', 'CANCEL_PO')")
	@PostMapping("/{id}/cancel")
	public ResponseEntity<PurchaseOrderResponse> cancel(@PathVariable Long id) {

		return ResponseEntity.ok(purchaseOrderService.cancelResponse(id));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'PURCHASING', 'RECEIVE_GOODS')")
	@PostMapping("/{id}/goods-receipts")
	public ResponseEntity<GoodsReceiptResponse> receive(@PathVariable Long id,
			@Valid @RequestBody CreateGoodsReceiptRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(goodsReceiptService.receiveResponse(id, request));
	}

	@GetMapping
	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'PURCHASING', 'VIEW_PO')")
	public ResponseEntity<PageResponse<PurchaseOrderSummaryResponse>> index(

			@RequestParam(required = false) String search,

			@RequestParam(required = false) String status,

			@RequestParam(required = false) Long supplierId,

			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,

			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,

			@RequestParam(defaultValue = "1") int page,

			int perPage) {

		final PurchaseOrderService purchaseOrderQueryService = null;
		return ResponseEntity.ok(purchaseOrderQueryService.getPurchaseOrders(search, status, supplierId, fromDate,
				toDate, page, perPage));
	}
}
