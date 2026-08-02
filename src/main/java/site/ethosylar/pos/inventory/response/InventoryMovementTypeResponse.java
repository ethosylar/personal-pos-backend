package site.ethosylar.pos.inventory.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InventoryMovementTypeResponse {

	private Long id;

	private String code;

	private String name;

	private String description;

	private Integer direction;

	private Boolean manualAllowed;
}