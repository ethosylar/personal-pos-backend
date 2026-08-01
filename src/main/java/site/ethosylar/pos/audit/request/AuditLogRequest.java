package site.ethosylar.pos.audit.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuditLogRequest {

    private Long actorUserId;

    private String actionCode;

    private String moduleCode;

    private String entityType;

    private Long entityId;

    private String description;

    private Object beforeData;

    private Object afterData;
}