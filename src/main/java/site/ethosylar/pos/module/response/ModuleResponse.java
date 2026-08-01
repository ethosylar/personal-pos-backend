package site.ethosylar.pos.module.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ModuleResponse {

    private Long id;

    private String code;

    private String name;

    private String description;

    private String route;

    private String icon;

    private Integer displayOrder;

    private Boolean active;
}