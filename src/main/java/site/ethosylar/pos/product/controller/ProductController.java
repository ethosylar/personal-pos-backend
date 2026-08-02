package site.ethosylar.pos.product.controller;

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
import site.ethosylar.pos.common.request.ChangeActiveStatusRequest;
import site.ethosylar.pos.common.response.PageResponse;
import site.ethosylar.pos.product.request.ChangeProductAvailabilityRequest;
import site.ethosylar.pos.product.request.ChangeProductPriceRequest;
import site.ethosylar.pos.product.request.CreateProductRequest;
import site.ethosylar.pos.product.request.ProductFilterRequest;
import site.ethosylar.pos.product.request.UpdateProductRequest;
import site.ethosylar.pos.product.response.ProductResponse;
import site.ethosylar.pos.product.service.ProductService;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'PRODUCT', 'VIEW')")
	@GetMapping
	public ResponseEntity<PageResponse<ProductResponse>> search(@Valid @ModelAttribute ProductFilterRequest filter) {

		return ResponseEntity.ok(productService.search(filter));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'PRODUCT', 'VIEW')")
	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {

		return ResponseEntity.ok(productService.getById(id));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'PRODUCT', 'CREATE')")
	@PostMapping
	public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'PRODUCT', 'UPDATE')")
	@PutMapping("/{id}")
	public ResponseEntity<ProductResponse> update(@PathVariable Long id,
			@Valid @RequestBody UpdateProductRequest request) {

		return ResponseEntity.ok(productService.update(id, request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'PRODUCT', 'CHANGE_PRICE')")
	@PatchMapping("/{id}/price")
	public ResponseEntity<ProductResponse> changePrice(@PathVariable Long id,
			@Valid @RequestBody ChangeProductPriceRequest request) {

		return ResponseEntity.ok(productService.changePrice(id, request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'PRODUCT', 'CHANGE_AVAILABILITY')")
	@PatchMapping("/{id}/availability")
	public ResponseEntity<ProductResponse> changeAvailability(@PathVariable Long id,
			@Valid @RequestBody ChangeProductAvailabilityRequest request) {

		return ResponseEntity.ok(productService.changeAvailability(id, request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'PRODUCT', 'CHANGE_STATUS')")
	@PatchMapping("/{id}/status")
	public ResponseEntity<ProductResponse> changeStatus(@PathVariable Long id,
			@Valid @RequestBody ChangeActiveStatusRequest request) {

		return ResponseEntity.ok(productService.changeStatus(id, request));
	}
}
