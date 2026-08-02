package site.ethosylar.pos.supplier.controller;

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
import site.ethosylar.pos.supplier.request.CreateSupplierRequest;
import site.ethosylar.pos.supplier.request.UpdateSupplierRequest;
import site.ethosylar.pos.supplier.response.SupplierResponse;
import site.ethosylar.pos.supplier.service.SupplierService;
import site.ethosylar.pos.common.request.ChangeActiveStatusRequest;
import site.ethosylar.pos.common.request.MasterDataFilterRequest;
import site.ethosylar.pos.common.response.PageResponse;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

	private final SupplierService supplierService;

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'SUPPLIER', 'VIEW')")
	@GetMapping
	public ResponseEntity<PageResponse<SupplierResponse>> getSupplier(
			@Valid @ModelAttribute MasterDataFilterRequest filter) {

		return ResponseEntity.ok(supplierService.search(filter));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'SUPPLIER', 'VIEW')")
	@GetMapping("/{id}")
	public ResponseEntity<SupplierResponse> getSupplier(@PathVariable Long id) {

		return ResponseEntity.ok(supplierService.getById(id));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'SUPPLIER', 'CREATE')")
	@PostMapping
	public ResponseEntity<SupplierResponse> createSupplier(@Valid @RequestBody CreateSupplierRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.create(request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'SUPPLIER', 'UPDATE')")
	@PutMapping("/{id}")
	public ResponseEntity<SupplierResponse> updateSupplier(@PathVariable Long id,
			@Valid @RequestBody UpdateSupplierRequest request) {

		return ResponseEntity.ok(supplierService.update(id, request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'SUPPLIER', " + "'CHANGE_STATUS')")
	@PatchMapping("/{id}/status")
	public ResponseEntity<SupplierResponse> changeSupplierStatus(@PathVariable Long id,
			@Valid @RequestBody ChangeActiveStatusRequest request) {

		return ResponseEntity.ok(supplierService.changeStatus(id, request));
	}
}
