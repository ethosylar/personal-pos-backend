package site.ethosylar.pos.procurement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.procurement.request.GenerateReorderPurchaseOrdersRequest;
import site.ethosylar.pos.procurement.response.PurchaseOrderResponse;
import site.ethosylar.pos.procurement.response.ReorderRecommendationResponse;
import site.ethosylar.pos.procurement.service.ReorderPurchaseOrderService;
import site.ethosylar.pos.procurement.service.ReorderRecommendationService;

@RestController
@RequestMapping("/api/procurement")
@RequiredArgsConstructor
public class ProcurementRecommendationController {

	private final ReorderRecommendationService reorderRecommendationService;

	private final ReorderPurchaseOrderService reorderPurchaseOrderService;

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'PURCHASING', 'VIEW')")
	@GetMapping("/reorder-recommendations")
	public ResponseEntity<List<ReorderRecommendationResponse>> getReorderRecommendations() {

		return ResponseEntity.ok(reorderRecommendationService.getRecommendations());
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'PURCHASING', 'CREATE_PO')")
	@PostMapping("/reorder-recommendations/draft-purchase-orders")
	public ResponseEntity<List<PurchaseOrderResponse>> generateDraftPurchaseOrders(

			@Valid @RequestBody GenerateReorderPurchaseOrdersRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(reorderPurchaseOrderService.generateDraftPurchaseOrders(request));
	}
}