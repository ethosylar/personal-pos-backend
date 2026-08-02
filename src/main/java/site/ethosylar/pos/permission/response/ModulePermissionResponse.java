package site.ethosylar.pos.permission.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ModulePermissionResponse {

    private Long id;

    private String code;

    private String name;

    private String description;

    private Boolean active;
}