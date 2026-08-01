package site.ethosylar.pos.role.response;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoleDetailResponse {

    private Long id;

    private String code;

    private String name;

    private String description;

    private Boolean system;

    private Boolean active;

    private List<RoleModuleResponse> modules;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}