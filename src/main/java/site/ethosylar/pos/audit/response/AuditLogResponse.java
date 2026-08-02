package site.ethosylar.pos.audit.response;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;
import tools.jackson.databind.JsonNode;

@Getter
@Builder
public class AuditLogResponse {

    private Long id;

    private AuditActorResponse actor;

    private AuditActionResponse action;

    private AuditModuleResponse module;

    private String entityType;

    private Long entityId;

    private String description;

    private JsonNode beforeData;

    private JsonNode afterData;

    private String ipAddress;

    private String userAgent;

    private OffsetDateTime createdAt;
}