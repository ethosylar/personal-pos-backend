package site.ethosylar.pos.role.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.module.mapper.ModuleMapper;
import site.ethosylar.pos.permission.entity.RoleModulePermissionEntity;
import site.ethosylar.pos.permission.mapper.ModulePermissionMapper;
import site.ethosylar.pos.role.entity.RoleEntity;
import site.ethosylar.pos.role.entity.RoleModuleEntity;
import site.ethosylar.pos.role.response.RoleDetailResponse;
import site.ethosylar.pos.role.response.RoleModuleResponse;
import site.ethosylar.pos.role.response.RoleResponse;

@Component
@RequiredArgsConstructor
public class RoleMapper {

    private final ModuleMapper moduleMapper;

    private final ModulePermissionMapper modulePermissionMapper;

    public RoleResponse toResponse(RoleEntity role) {

        return RoleResponse.builder()
                .id(role.getId())
                .code(role.getCode())
                .name(role.getName())
                .description(role.getDescription())
                .system(role.getSystem())
                .active(role.getActive())
                .build();
    }

    public RoleDetailResponse toDetailResponse(
            RoleEntity role,
            List<RoleModuleEntity> roleModules,
            Map<Long, List<RoleModulePermissionEntity>> permissionsByRoleModule) {

        List<RoleModuleResponse> modules;

        if (roleModules == null) {
            modules = Collections.emptyList();
        } else {
            modules = roleModules.stream()
                    .map(roleModule -> {

                        List<RoleModulePermissionEntity> permissions =
                                permissionsByRoleModule.getOrDefault(
                                        roleModule.getId(),
                                        Collections.emptyList()
                                );

                        return RoleModuleResponse.builder()
                                .roleModuleId(roleModule.getId())
                                .module(
                                    moduleMapper.toResponse(
                                        roleModule.getModule()
                                    )
                                )
                                .permissions(
                                    permissions.stream()
                                        .map(permission ->
                                            modulePermissionMapper.toResponse(
                                                permission.getModulePermission()
                                            )
                                        )
                                        .toList()
                                )
                                .build();
                    })
                    .toList();
        }

        return RoleDetailResponse.builder()
                .id(role.getId())
                .code(role.getCode())
                .name(role.getName())
                .description(role.getDescription())
                .system(role.getSystem())
                .active(role.getActive())
                .modules(modules)
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}