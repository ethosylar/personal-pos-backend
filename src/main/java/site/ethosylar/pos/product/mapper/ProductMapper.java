package site.ethosylar.pos.product.mapper;

import org.springframework.stereotype.Component;

import site.ethosylar.pos.product.entity.ProductEntity;
import site.ethosylar.pos.product.projection.ProductSummaryProjection;
import site.ethosylar.pos.product.response.ProductResponse;
import site.ethosylar.pos.product.response.ProductSummaryResponse;

@Component
public class ProductMapper {

	public ProductResponse toResponse(ProductEntity entity) {

		return ProductResponse.builder().id(entity.getId()).code(entity.getCode()).name(entity.getName())

				.categoryId(entity.getCategory().getId()).categoryCode(entity.getCategory().getCode())
				.categoryName(entity.getCategory().getName())

				.sku(entity.getSku()).barcode(entity.getBarcode())

				.description(entity.getDescription())

				.sellingPrice(entity.getSellingPrice())

				.calculatedCost(null)

				.displayOrder(entity.getDisplayOrder())

				.available(entity.getAvailable())

				.active(entity.getActive())

				.createdAt(entity.getCreatedAt())

				.updatedAt(entity.getUpdatedAt())

				.build();
	}
}