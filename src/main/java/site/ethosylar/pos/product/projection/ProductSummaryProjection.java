package site.ethosylar.pos.product.projection;

import java.math.BigDecimal;

public interface ProductSummaryProjection {

	Long getId();

	String getCode();

	String getName();

	Long getCategoryId();

	String getCategoryName();

	BigDecimal getSellingPrice();

	Boolean getActive();
}