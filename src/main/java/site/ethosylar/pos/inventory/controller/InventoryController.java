package site.ethosylar.pos.inventory.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.config.ApiPaths;
import site.ethosylar.pos.common.response.PageResponse;
import site.ethosylar.pos.inventory.request.InventoryBalanceFilterRequest;
import site.ethosylar.pos.inventory.request.InventoryMovementFilterRequest;
import site.ethosylar.pos.inventory.request.InventoryReturnRequest;
import site.ethosylar.pos.inventory.request.ManualInventoryMovementRequest;
import site.ethosylar.pos.inventory.request.StockAdjustmentRequest;
import site.ethosylar.pos.inventory.request.UpdateStockPolicyRequest;
import site.ethosylar.pos.inventory.response.InventoryBalanceResponse;
import site.ethosylar.pos.inventory.response.InventoryMovementResponse;
import site.ethosylar.pos.inventory.response.InventoryStockPolicyResponse;
import site.ethosylar.pos.inventory.service.InventoryMovementService;
import site.ethosylar.pos.inventory.service.InventoryQueryService;
import site.ethosylar.pos.inventory.service.InventoryStockPolicyService;

@RestController
@RequestMapping(ApiPaths.V1 + "/inventory")
@RequiredArgsConstructor
public class InventoryController {

	private final InventoryQueryService inventoryQueryService;

	private final InventoryMovementService inventoryMovementService;

	private final InventoryStockPolicyService stockPolicyService;

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'INVENTORY', 'VIEW')")
	@GetMapping("/balances")
	public ResponseEntity<PageResponse<InventoryBalanceResponse>> getBalances(
			@Valid @ModelAttribute InventoryBalanceFilterRequest filter) {

		return ResponseEntity.ok(inventoryQueryService.searchBalances(filter));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'INVENTORY', 'VIEW')")
	@GetMapping("/balances/{ingredientId}")
	public ResponseEntity<InventoryBalanceResponse> getBalance(@PathVariable Long ingredientId) {

		return ResponseEntity.ok(inventoryQueryService.getBalance(ingredientId));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'INVENTORY', 'VIEW')")
	@GetMapping("/movements")
	public ResponseEntity<PageResponse<InventoryMovementResponse>> getMovements(
			@Valid @ModelAttribute InventoryMovementFilterRequest filter) {

		return ResponseEntity.ok(inventoryQueryService.searchMovements(filter));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'INVENTORY', 'RECORD_OPENING_STOCK')")
	@PostMapping("/opening-stock")
	public ResponseEntity<InventoryMovementResponse> recordOpeningStock(
			@Valid @RequestBody ManualInventoryMovementRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMovementService.recordOpeningStock(request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'INVENTORY', 'RECORD_WASTE')")
	@PostMapping("/waste")
	public ResponseEntity<InventoryMovementResponse> recordWaste(
			@Valid @RequestBody ManualInventoryMovementRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMovementService.recordWaste(request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'INVENTORY', 'STOCK_OUT')")
	@PostMapping("/manual-usage")
	public ResponseEntity<InventoryMovementResponse> recordManualUsage(
			@Valid @RequestBody ManualInventoryMovementRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMovementService.recordManualUsage(request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'INVENTORY', 'ADJUST_STOCK')")
	@PostMapping("/adjustments")
	public ResponseEntity<InventoryMovementResponse> recordAdjustment(
			@Valid @RequestBody StockAdjustmentRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMovementService.recordAdjustment(request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'INVENTORY', 'STOCK_IN') " + "or "
			+ "@permissionService.hasPermission(" + "authentication, 'INVENTORY', 'STOCK_OUT')")
	@PostMapping("/returns")
	public ResponseEntity<InventoryMovementResponse> recordReturn(@Valid @RequestBody InventoryReturnRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMovementService.recordReturn(request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'INVENTORY', 'VIEW')")
	@GetMapping("/stock-policies")
	public ResponseEntity<List<InventoryStockPolicyResponse>> getStockPolicies() {

		return ResponseEntity.ok(stockPolicyService.getPolicies());
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'INVENTORY', 'VIEW')")
	@GetMapping("/ingredients/{ingredientId}/stock-policy")
	public ResponseEntity<InventoryStockPolicyResponse> getStockPolicy(

			@PathVariable Long ingredientId) {

		return ResponseEntity.ok(stockPolicyService.getPolicy(ingredientId));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'INVENTORY', " + "'MANAGE_STOCK_POLICY')")
	@PutMapping("/ingredients/{ingredientId}/stock-policy")
	public ResponseEntity<InventoryStockPolicyResponse> updateStockPolicy(

			@PathVariable Long ingredientId,

			@Valid @RequestBody UpdateStockPolicyRequest request) {

		return ResponseEntity.ok(stockPolicyService.updatePolicy(ingredientId, request));
	}
}