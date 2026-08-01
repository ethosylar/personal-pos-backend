package site.ethosylar.pos.audit.mapper;

import org.springframework.stereotype.Component;

import site.ethosylar.pos.audit.entity.AuditLogEntity;
import site.ethosylar.pos.audit.response.AuditActionResponse;
import site.ethosylar.pos.audit.response.AuditActorResponse;
import site.ethosylar.pos.audit.response.AuditLogResponse;
import site.ethosylar.pos.audit.response.AuditModuleResponse;

@Component
public class AuditLogMapper {

	public AuditLogResponse toResponse(AuditLogEntity entity) {

		AuditActorResponse actor = null;

		if (entity.getUser() != null) {

			actor = AuditActorResponse.builder().id(entity.getUser().getId()).username(entity.getUser().getUsername())
					.fullName(entity.getUser().getFullName()).build();
		}

		AuditActionResponse action = AuditActionResponse.builder().id(entity.getAction().getId())
				.code(entity.getAction().getCode()).name(entity.getAction().getName()).build();

		AuditModuleResponse module = null;

		if (entity.getModule() != null) {

			module = AuditModuleResponse.builder().id(entity.getModule().getId()).code(entity.getModule().getCode())
					.name(entity.getModule().getName()).build();
		}

		return AuditLogResponse.builder().id(entity.getId()).actor(actor).action(action).module(module)
				.entityType(entity.getEntityType()).entityId(entity.getEntityId()).description(entity.getDescription())
				.beforeData(entity.getBeforeData()).afterData(entity.getAfterData()).ipAddress(entity.getIpAddress())
				.userAgent(entity.getUserAgent()).createdAt(entity.getCreatedAt()).build();
	}
}