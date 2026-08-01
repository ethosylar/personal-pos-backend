package site.ethosylar.pos.audit.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuditActionResponse {

    private Long id;

    private String code;

    private String name;
}