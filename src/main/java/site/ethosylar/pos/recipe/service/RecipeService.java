package site.ethosylar.pos.recipe.service;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.request.AuditLogRequest;
import site.ethosylar.pos.audit.service.AuditService;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.DuplicateResourceException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.ingredient.entity.IngredientEntity;
import site.ethosylar.pos.ingredient.repository.IngredientRepository;
import site.ethosylar.pos.product.entity.ProductEntity;
import site.ethosylar.pos.product.repository.ProductRepository;
import site.ethosylar.pos.recipe.entity.RecipeEntity;
import site.ethosylar.pos.recipe.entity.RecipeItemEntity;
import site.ethosylar.pos.recipe.mapper.RecipeMapper;
import site.ethosylar.pos.recipe.model.RecipeCostingResult;
import site.ethosylar.pos.recipe.model.RecipeItemCostResult;
import site.ethosylar.pos.recipe.repository.RecipeItemRepository;
import site.ethosylar.pos.recipe.repository.RecipeRepository;
import site.ethosylar.pos.recipe.request.CreateRecipeItemRequest;
import site.ethosylar.pos.recipe.request.CreateRecipeRequest;
import site.ethosylar.pos.recipe.request.UpdateRecipeItemRequest;
import site.ethosylar.pos.recipe.request.UpdateRecipeRequest;
import site.ethosylar.pos.recipe.response.RecipeDetailResponse;
import site.ethosylar.pos.recipe.response.RecipeSummaryResponse;

@Service
@RequiredArgsConstructor
@Transactional
public class RecipeService {

	private final RecipeRepository recipeRepository;
	private final RecipeItemRepository itemRepository;

	private final ProductRepository productRepository;
	private final IngredientRepository ingredientRepository;

	private final RecipeCostingService costingService;
	private final RecipeMapper recipeMapper;

	private final AuditService auditService;

	@Transactional(readOnly = true)
	public List<RecipeSummaryResponse> getProductRecipes(Long productId) {

		getProduct(productId);

		return recipeRepository.findByProduct_IdOrderByVersionNoDesc(productId).stream()
				.map(recipeMapper::toSummaryResponse).toList();
	}

	@Transactional(readOnly = true)
	public RecipeDetailResponse getById(Long recipeId) {

		return buildDetailResponse(getRecipe(recipeId));
	}

	@Transactional(readOnly = true)
	public RecipeDetailResponse getCurrentRecipe(Long productId) {

		getProduct(productId);

		RecipeEntity recipe = recipeRepository.findByProduct_IdAndCurrentTrue(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Current recipe not found for product."));

		return buildDetailResponse(recipe);
	}

	public RecipeDetailResponse create(Long productId, CreateRecipeRequest request) {

		ProductEntity product = getActiveProduct(productId);

		int nextVersion = recipeRepository.findTopByProduct_IdOrderByVersionNoDesc(productId)
				.map(recipe -> recipe.getVersionNo() + 1).orElse(1);

		RecipeEntity recipe = new RecipeEntity();

		recipe.setProduct(product);
		recipe.setVersionNo(nextVersion);

		recipe.setYieldQuantity(request.getYieldQuantity());

		recipe.setNotes(normalize(request.getNotes()));

		recipe.setCurrent(false);

		recipe = recipeRepository.save(recipe);

		auditService.log(AuditLogRequest.builder().actionCode("CREATE").moduleCode("RECIPE").entityType("RECIPE")
				.entityId(recipe.getId()).description("Recipe version created.").afterData(snapshot(recipe)).build());

		return buildDetailResponse(recipe);
	}

	public RecipeDetailResponse update(Long recipeId, UpdateRecipeRequest request) {

		RecipeEntity recipe = getRecipe(recipeId);

		ensureDraft(recipe);

		Map<String, Object> before = snapshot(recipe);

		recipe.setYieldQuantity(request.getYieldQuantity());

		recipe.setNotes(normalize(request.getNotes()));

		recipeRepository.save(recipe);

		auditService.log(AuditLogRequest.builder().actionCode("UPDATE").moduleCode("RECIPE").entityType("RECIPE")
				.entityId(recipe.getId()).description("Draft recipe updated.").beforeData(before)
				.afterData(snapshot(recipe)).build());

		return buildDetailResponse(recipe);
	}

	public RecipeDetailResponse addItem(Long recipeId, CreateRecipeItemRequest request) {

		RecipeEntity recipe = getRecipe(recipeId);

		ensureDraft(recipe);

		IngredientEntity ingredient = getActiveIngredient(request.getIngredientId());

		if (itemRepository.existsByRecipe_IdAndIngredient_Id(recipeId, ingredient.getId())) {

			throw new DuplicateResourceException("Ingredient is already included in this recipe.");
		}

		RecipeItemEntity item = new RecipeItemEntity();

		item.setRecipe(recipe);
		item.setIngredient(ingredient);

		item.setQuantity(request.getQuantity());

		item.setWastePercent(request.getWastePercent());

		item.setDisplayOrder(request.getDisplayOrder());

		item.setNotes(normalize(request.getNotes()));

		item = itemRepository.save(item);

		auditService.log(AuditLogRequest.builder().actionCode("CREATE").moduleCode("RECIPE").entityType("RECIPE_ITEM")
				.entityId(item.getId()).description("Recipe ingredient added.").afterData(snapshotItem(item)).build());

		return buildDetailResponse(recipe);
	}

	public RecipeDetailResponse updateItem(Long recipeId, Long itemId, UpdateRecipeItemRequest request) {

		RecipeEntity recipe = getRecipe(recipeId);

		ensureDraft(recipe);

		RecipeItemEntity item = getRecipeItem(recipeId, itemId);

		Map<String, Object> before = snapshotItem(item);

		item.setQuantity(request.getQuantity());

		item.setWastePercent(request.getWastePercent());

		item.setDisplayOrder(request.getDisplayOrder());

		item.setNotes(normalize(request.getNotes()));

		itemRepository.save(item);

		auditService.log(AuditLogRequest.builder().actionCode("UPDATE").moduleCode("RECIPE").entityType("RECIPE_ITEM")
				.entityId(item.getId()).description("Recipe ingredient updated.").beforeData(before)
				.afterData(snapshotItem(item)).build());

		return buildDetailResponse(recipe);
	}

	public void deleteItem(Long recipeId, Long itemId) {

		RecipeEntity recipe = getRecipe(recipeId);

		ensureDraft(recipe);

		RecipeItemEntity item = getRecipeItem(recipeId, itemId);

		Map<String, Object> before = snapshotItem(item);

		itemRepository.delete(item);

		auditService.log(AuditLogRequest.builder().actionCode("DELETE").moduleCode("RECIPE").entityType("RECIPE_ITEM")
				.entityId(item.getId()).description("Recipe ingredient removed.").beforeData(before).build());
	}

	public RecipeDetailResponse activate(Long recipeId) {

		RecipeEntity recipe = getRecipe(recipeId);

		ensureDraft(recipe);

		if (!Boolean.TRUE.equals(recipe.getProduct().getActive())) {

			throw new BusinessRuleException("Cannot activate a recipe for an inactive product.");
		}

		List<RecipeItemEntity> items = getItems(recipeId);

		if (items.isEmpty()) {

			throw new BusinessRuleException("Recipe must contain at least one ingredient before activation.");
		}

		for (RecipeItemEntity item : items) {

			if (!Boolean.TRUE.equals(item.getIngredient().getActive())) {

				throw new BusinessRuleException("Ingredient " + item.getIngredient().getCode() + " is inactive.");
			}
		}

		RecipeCostingResult costing = costingService.calculate(recipe, items);

		if (!Boolean.TRUE.equals(costing.getAllCostsAvailable())) {

			throw new BusinessRuleException(
					"Recipe cannot be activated because one or more ingredients do not have an active preferred supplier.");
		}

		OffsetDateTime now = OffsetDateTime.now();

		recipeRepository.findByProduct_IdAndCurrentTrue(recipe.getProduct().getId()).ifPresent(current -> {

			if (!current.getId().equals(recipe.getId())) {

				current.setCurrent(false);
				current.setEffectiveTo(now);

				/*
				 * Important because PostgreSQL's unique partial index only allows one current
				 * version.
				 */
				recipeRepository.saveAndFlush(current);
			}
		});

		for (RecipeItemEntity item : items) {

			RecipeItemCostResult cost = findCostResult(costing, item.getId());

			item.setUnitCostSnapshot(cost.getUnitCost());

			item.setLineCostSnapshot(cost.getLineCost());
		}

		itemRepository.saveAll(items);

		recipe.setCurrent(true);
		recipe.setEffectiveFrom(now);
		recipe.setEffectiveTo(null);
		recipe.setActivatedAt(now);

		recipe.setActivatedCostSnapshot(costing.getCostPerPortion());

		recipeRepository.save(recipe);

		auditService.log(AuditLogRequest.builder().actionCode("RECIPE_ACTIVATE").moduleCode("RECIPE")
				.entityType("RECIPE").entityId(recipe.getId()).description("Recipe version activated.")
				.afterData(snapshot(recipe)).build());

		return buildDetailResponse(recipe);
	}

	private RecipeDetailResponse buildDetailResponse(RecipeEntity recipe) {

		List<RecipeItemEntity> items = getItems(recipe.getId());

		RecipeCostingResult costing = costingService.calculate(recipe, items);

		return recipeMapper.toDetailResponse(recipe, items, costing);
	}

	private List<RecipeItemEntity> getItems(Long recipeId) {

		return itemRepository.findByRecipe_IdOrderByDisplayOrderAscIdAsc(recipeId);
	}

	private RecipeEntity getRecipe(Long id) {

		return recipeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recipe not found."));
	}

	private RecipeItemEntity getRecipeItem(Long recipeId, Long itemId) {

		return itemRepository.findByIdAndRecipe_Id(itemId, recipeId)
				.orElseThrow(() -> new ResourceNotFoundException("Recipe item not found."));
	}

	private ProductEntity getProduct(Long id) {

		return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found."));
	}

	private ProductEntity getActiveProduct(Long id) {

		ProductEntity product = getProduct(id);

		if (!Boolean.TRUE.equals(product.getActive())) {

			throw new BusinessRuleException("Product is inactive.");
		}

		return product;
	}

	private IngredientEntity getActiveIngredient(Long id) {

		IngredientEntity ingredient = ingredientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Ingredient not found."));

		if (!Boolean.TRUE.equals(ingredient.getActive())) {

			throw new BusinessRuleException("Ingredient is inactive.");
		}

		return ingredient;
	}

	private void ensureDraft(RecipeEntity recipe) {

		if (recipe.getActivatedAt() != null || Boolean.TRUE.equals(recipe.getCurrent())) {

			throw new BusinessRuleException(
					"Activated or retired recipe versions cannot be modified. Create a new recipe version instead.");
		}
	}

	private RecipeItemCostResult findCostResult(RecipeCostingResult costing, Long itemId) {

		return costing.getItems().stream().filter(item -> itemId.equals(item.getRecipeItemId())).findFirst()
				.orElseThrow();
	}

	private Map<String, Object> snapshot(RecipeEntity recipe) {

		Map<String, Object> data = new LinkedHashMap<>();

		data.put("product", recipe.getProduct().getCode());

		data.put("versionNo", recipe.getVersionNo());

		data.put("yieldQuantity", recipe.getYieldQuantity());

		data.put("current", recipe.getCurrent());

		data.put("effectiveFrom", recipe.getEffectiveFrom());

		data.put("effectiveTo", recipe.getEffectiveTo());

		data.put("activatedAt", recipe.getActivatedAt());

		data.put("activatedCostSnapshot", recipe.getActivatedCostSnapshot());

		return data;
	}

	private Map<String, Object> snapshotItem(RecipeItemEntity item) {

		Map<String, Object> data = new LinkedHashMap<>();

		data.put("recipeId", item.getRecipe().getId());

		data.put("ingredient", item.getIngredient().getCode());

		data.put("quantity", item.getQuantity());

		data.put("wastePercent", item.getWastePercent());

		data.put("displayOrder", item.getDisplayOrder());

		data.put("unitCostSnapshot", item.getUnitCostSnapshot());

		data.put("lineCostSnapshot", item.getLineCostSnapshot());

		return data;
	}

	private String normalize(String value) {

		if (value == null || value.isBlank()) {

			return null;
		}

		return value.trim();
	}
}