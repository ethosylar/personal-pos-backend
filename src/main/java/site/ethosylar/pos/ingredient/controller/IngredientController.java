package site.ethosylar.pos.ingredient.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.config.ApiPaths;
import site.ethosylar.pos.common.request.ChangeActiveStatusRequest;
import site.ethosylar.pos.common.response.PageResponse;
import site.ethosylar.pos.ingredient.request.CreateIngredientRequest;
import site.ethosylar.pos.ingredient.request.CreateIngredientSupplierRequest;
import site.ethosylar.pos.ingredient.request.IngredientFilterRequest;
import site.ethosylar.pos.ingredient.request.UpdateIngredientRequest;
import site.ethosylar.pos.ingredient.request.UpdateIngredientSupplierRequest;
import site.ethosylar.pos.ingredient.response.IngredientDetailResponse;
import site.ethosylar.pos.ingredient.response.IngredientSummaryResponse;
import site.ethosylar.pos.ingredient.response.IngredientSupplierResponse;
import site.ethosylar.pos.ingredient.service.IngredientService;
import site.ethosylar.pos.ingredient.service.IngredientSupplierService;

@RestController
@RequestMapping(ApiPaths.V1 + "/ingredients")
@RequiredArgsConstructor
public class IngredientController {

	private final IngredientService ingredientService;

	private final IngredientSupplierService ingredientSupplierService;

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'INGREDIENT', 'VIEW')")
	@GetMapping
	public ResponseEntity<PageResponse<IngredientSummaryResponse>> search(
			@Valid @ModelAttribute IngredientFilterRequest filter) {

		return ResponseEntity.ok(ingredientService.search(filter));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'INGREDIENT', 'VIEW')")
	@GetMapping("/{id}")
	public ResponseEntity<IngredientDetailResponse> getById(@PathVariable Long id) {

		return ResponseEntity.ok(ingredientService.getById(id));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'INGREDIENT', 'CREATE')")
	@PostMapping
	public ResponseEntity<IngredientDetailResponse> create(@Valid @RequestBody CreateIngredientRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(ingredientService.create(request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'INGREDIENT', 'UPDATE')")
	@PutMapping("/{id}")
	public ResponseEntity<IngredientDetailResponse> update(@PathVariable Long id,
			@Valid @RequestBody UpdateIngredientRequest request) {

		return ResponseEntity.ok(ingredientService.update(id, request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'INGREDIENT', 'CHANGE_STATUS')")
	@PatchMapping("/{id}/status")
	public ResponseEntity<IngredientDetailResponse> changeStatus(@PathVariable Long id,
			@Valid @RequestBody ChangeActiveStatusRequest request) {

		return ResponseEntity.ok(ingredientService.changeStatus(id, request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'INGREDIENT', 'VIEW')")
	@GetMapping("/{ingredientId}/suppliers")
	public ResponseEntity<List<IngredientSupplierResponse>> getSuppliers(@PathVariable Long ingredientId) {

		return ResponseEntity.ok(ingredientSupplierService.getSuppliers(ingredientId));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'INGREDIENT', 'MANAGE_SUPPLIERS')")
	@PostMapping("/{ingredientId}/suppliers")
	public ResponseEntity<IngredientSupplierResponse> addSupplier(@PathVariable Long ingredientId,

			@Valid @RequestBody CreateIngredientSupplierRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(ingredientSupplierService.create(ingredientId, request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'INGREDIENT', 'MANAGE_SUPPLIERS')")
	@PutMapping("/{ingredientId}/suppliers/{supplierOptionId}")
	public ResponseEntity<IngredientSupplierResponse> updateSupplier(@PathVariable Long ingredientId,
			@PathVariable Long supplierOptionId,

			@Valid @RequestBody UpdateIngredientSupplierRequest request) {

		return ResponseEntity.ok(ingredientSupplierService.update(ingredientId, supplierOptionId, request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'INGREDIENT', 'MANAGE_SUPPLIERS')")
	@PatchMapping("/{ingredientId}/suppliers/{supplierOptionId}/status")
	public ResponseEntity<IngredientSupplierResponse> changeSupplierStatus(@PathVariable Long ingredientId,
			@PathVariable Long supplierOptionId,

			@Valid @RequestBody ChangeActiveStatusRequest request) {

		return ResponseEntity.ok(ingredientSupplierService.changeStatus(ingredientId, supplierOptionId, request));
	}
}