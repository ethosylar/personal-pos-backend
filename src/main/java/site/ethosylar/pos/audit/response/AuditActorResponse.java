package site.ethosylar.pos.audit.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuditActorResponse {

    private Long id;

    private String username;

    private String fullName;
}