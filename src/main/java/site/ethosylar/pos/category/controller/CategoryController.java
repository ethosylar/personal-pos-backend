package site.ethosylar.pos.category.controller;

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
import site.ethosylar.pos.category.request.CreateCategoryRequest;
import site.ethosylar.pos.category.request.UpdateCategoryRequest;
import site.ethosylar.pos.category.response.CategoryResponse;
import site.ethosylar.pos.category.service.IngredientCategoryService;
import site.ethosylar.pos.category.service.ProductCategoryService;
import site.ethosylar.pos.common.config.ApiPaths;
import site.ethosylar.pos.common.request.ChangeActiveStatusRequest;
import site.ethosylar.pos.common.request.MasterDataFilterRequest;
import site.ethosylar.pos.common.response.PageResponse;

@RestController
@RequestMapping(ApiPaths.V1 + "/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final ProductCategoryService productCategoryService;

	private final IngredientCategoryService ingredientCategoryService;

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'CATEGORY', 'VIEW')")
	@GetMapping("/products")
	public ResponseEntity<PageResponse<CategoryResponse>> getProductCategories(
			@Valid @ModelAttribute MasterDataFilterRequest filter) {

		return ResponseEntity.ok(productCategoryService.search(filter));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'CATEGORY', 'VIEW')")
	@GetMapping("/products/{id}")
	public ResponseEntity<CategoryResponse> getProductCategory(@PathVariable Long id) {

		return ResponseEntity.ok(productCategoryService.getById(id));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'CATEGORY', 'CREATE')")
	@PostMapping("/products")
	public ResponseEntity<CategoryResponse> createProductCategory(@Valid @RequestBody CreateCategoryRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(productCategoryService.create(request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'CATEGORY', 'UPDATE')")
	@PutMapping("/products/{id}")
	public ResponseEntity<CategoryResponse> updateProductCategory(@PathVariable Long id,
			@Valid @RequestBody UpdateCategoryRequest request) {

		return ResponseEntity.ok(productCategoryService.update(id, request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'CATEGORY', " + "'CHANGE_STATUS')")
	@PatchMapping("/products/{id}/status")
	public ResponseEntity<CategoryResponse> changeProductCategoryStatus(@PathVariable Long id,
			@Valid @RequestBody ChangeActiveStatusRequest request) {

		return ResponseEntity.ok(productCategoryService.changeStatus(id, request));
	}

	/*
	 * Repeat the same endpoint pattern using:
	 *
	 * /ingredients /ingredients/{id} /ingredients/{id}/status
	 *
	 * and IngredientCategoryService.
	 */
	
	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'CATEGORY', 'VIEW')")
	@GetMapping("/ingredients")
	public ResponseEntity<PageResponse<CategoryResponse>> getIngredientCategories(
			@Valid @ModelAttribute MasterDataFilterRequest filter) {

		return ResponseEntity.ok(ingredientCategoryService.search(filter));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'CATEGORY', 'VIEW')")
	@GetMapping("/ingredients/{id}")
	public ResponseEntity<CategoryResponse> getIngredientCategory(@PathVariable Long id) {

		return ResponseEntity.ok(ingredientCategoryService.getById(id));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'CATEGORY', 'CREATE')")
	@PostMapping("/ingredients")
	public ResponseEntity<CategoryResponse> createIngredientCategory(@Valid @RequestBody CreateCategoryRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(ingredientCategoryService.create(request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'CATEGORY', 'UPDATE')")
	@PutMapping("/ingredients/{id}")
	public ResponseEntity<CategoryResponse> updateIngredientCategory(@PathVariable Long id,
			@Valid @RequestBody UpdateCategoryRequest request) {

		return ResponseEntity.ok(ingredientCategoryService.update(id, request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, " + "'CATEGORY', " + "'CHANGE_STATUS')")
	@PatchMapping("/ingredients/{id}/status")
	public ResponseEntity<CategoryResponse> changeIngredientCategoryStatus(@PathVariable Long id,
			@Valid @RequestBody ChangeActiveStatusRequest request) {

		return ResponseEntity.ok(ingredientCategoryService.changeStatus(id, request));
	}
}