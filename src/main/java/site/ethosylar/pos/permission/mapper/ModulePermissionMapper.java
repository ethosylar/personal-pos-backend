package site.ethosylar.pos.permission.mapper;

import org.springframework.stereotype.Component;

import site.ethosylar.pos.permission.entity.ModulePermissionEntity;
import site.ethosylar.pos.permission.response.ModulePermissionResponse;

@Component
public class ModulePermissionMapper {

	public ModulePermissionResponse toResponse(ModulePermissionEntity entity) {

		return ModulePermissionResponse.builder().id(entity.getId()).code(entity.getCode()).name(entity.getName())
				.description(entity.getDescription()).active(entity.getActive()).build();
	}
}