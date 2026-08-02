package site.ethosylar.pos.role.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import site.ethosylar.pos.module.response.ModuleResponse;
import site.ethosylar.pos.permission.response.ModulePermissionResponse;

@Getter
@Builder
public class RoleModuleResponse {

    private Long roleModuleId;

    private ModuleResponse module;

    private List<ModulePermissionResponse> permissions;
}