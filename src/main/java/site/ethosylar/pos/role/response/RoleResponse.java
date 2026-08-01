package site.ethosylar.pos.role.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoleResponse {

    private Long id;

    private String code;

    private String name;

    private String description;

    private Boolean system;

    private Boolean active;
}