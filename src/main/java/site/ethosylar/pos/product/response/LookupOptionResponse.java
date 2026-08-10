package site.ethosylar.pos.product.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LookupOptionResponse {

	private Long id;

	private String code;

	private String name;
}
