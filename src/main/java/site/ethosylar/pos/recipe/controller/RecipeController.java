package site.ethosylar.pos.recipe.controller;

import java.util.List;

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
import site.ethosylar.pos.recipe.request.CreateRecipeItemRequest;
import site.ethosylar.pos.recipe.request.CreateRecipeRequest;
import site.ethosylar.pos.recipe.request.UpdateRecipeItemRequest;
import site.ethosylar.pos.recipe.request.UpdateRecipeRequest;
import site.ethosylar.pos.recipe.response.RecipeDetailResponse;
import site.ethosylar.pos.recipe.response.RecipeSummaryResponse;
import site.ethosylar.pos.recipe.service.RecipeService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecipeController {

	private final RecipeService recipeService;

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'RECIPE', 'VIEW')")
	@GetMapping("/products/{productId}/recipes")
	public ResponseEntity<List<RecipeSummaryResponse>> getProductRecipes(@PathVariable Long productId) {

		return ResponseEntity.ok(recipeService.getProductRecipes(productId));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'RECIPE', 'VIEW')")
	@GetMapping("/products/{productId}/recipes/current")
	public ResponseEntity<RecipeDetailResponse> getCurrentRecipe(@PathVariable Long productId) {

		return ResponseEntity.ok(recipeService.getCurrentRecipe(productId));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'RECIPE', 'VIEW')")
	@GetMapping("/recipes/{recipeId}")
	public ResponseEntity<RecipeDetailResponse> getById(@PathVariable Long recipeId) {

		return ResponseEntity.ok(recipeService.getById(recipeId));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'RECIPE', 'CREATE')")
	@PostMapping("/products/{productId}/recipes")
	public ResponseEntity<RecipeDetailResponse> create(@PathVariable Long productId,
			@Valid @RequestBody CreateRecipeRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(recipeService.create(productId, request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'RECIPE', 'UPDATE')")
	@PutMapping("/recipes/{recipeId}")
	public ResponseEntity<RecipeDetailResponse> update(@PathVariable Long recipeId,
			@Valid @RequestBody UpdateRecipeRequest request) {

		return ResponseEntity.ok(recipeService.update(recipeId, request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'RECIPE', 'UPDATE')")
	@PostMapping("/recipes/{recipeId}/items")
	public ResponseEntity<RecipeDetailResponse> addItem(@PathVariable Long recipeId,
			@Valid @RequestBody CreateRecipeItemRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(recipeService.addItem(recipeId, request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'RECIPE', 'UPDATE')")
	@PutMapping("/recipes/{recipeId}/items/{itemId}")
	public ResponseEntity<RecipeDetailResponse> updateItem(@PathVariable Long recipeId, @PathVariable Long itemId,
			@Valid @RequestBody UpdateRecipeItemRequest request) {

		return ResponseEntity.ok(recipeService.updateItem(recipeId, itemId, request));
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'RECIPE', 'UPDATE')")
	@DeleteMapping("/recipes/{recipeId}/items/{itemId}")
	public ResponseEntity<Void> deleteItem(@PathVariable Long recipeId, @PathVariable Long itemId) {

		recipeService.deleteItem(recipeId, itemId);

		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("@permissionService.hasPermission(" + "authentication, 'RECIPE', 'ACTIVATE')")
	@PostMapping("/recipes/{recipeId}/activate")
	public ResponseEntity<RecipeDetailResponse> activate(@PathVariable Long recipeId) {

		return ResponseEntity.ok(recipeService.activate(recipeId));
	}
}