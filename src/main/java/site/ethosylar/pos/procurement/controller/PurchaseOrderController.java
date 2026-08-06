package site.ethosylar.pos.procurement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.config.ApiPaths;
import site.ethosylar.pos.procurement.request.CreateGoodsReceiptRequest;
import site.ethosylar.pos.procurement.request.CreatePurchaseOrderRequest;
import site.ethosylar.pos.procurement.response.GoodsReceiptResponse;
import site.ethosylar.pos.procurement.response.PurchaseOrderResponse;
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
}
