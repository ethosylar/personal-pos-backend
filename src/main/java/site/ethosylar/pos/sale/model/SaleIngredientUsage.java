package site.ethosylar.pos.sale.model;

import java.math.BigDecimal;

public record SaleIngredientUsage(Long ingredientId, BigDecimal quantity, BigDecimal unitCost) {
}