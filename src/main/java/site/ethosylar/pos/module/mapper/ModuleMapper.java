package site.ethosylar.pos.module.mapper;

import org.springframework.stereotype.Component;

import site.ethosylar.pos.module.entity.ModuleEntity;
import site.ethosylar.pos.module.response.ModuleResponse;

@Component
public class ModuleMapper {

    public ModuleResponse toResponse(ModuleEntity entity) {

        return ModuleResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .route(entity.getRoute())
                .icon(entity.getIcon())
                .displayOrder(entity.getDisplayOrder())
                .active(entity.getActive())
                .build();
    }
}