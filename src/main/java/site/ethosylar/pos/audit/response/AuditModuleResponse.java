package site.ethosylar.pos.audit.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuditModuleResponse {

    private Long id;

    private String code;

    private String name;
}