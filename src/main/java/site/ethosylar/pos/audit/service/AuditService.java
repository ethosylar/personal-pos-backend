package site.ethosylar.pos.audit.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.entity.AuditActionEntity;
import site.ethosylar.pos.audit.entity.AuditLogEntity;
import site.ethosylar.pos.audit.repository.AuditActionRepository;
import site.ethosylar.pos.audit.repository.AuditLogRepository;
import site.ethosylar.pos.audit.request.AuditLogRequest;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.module.entity.ModuleEntity;
import site.ethosylar.pos.module.repository.ModuleRepository;
import site.ethosylar.pos.user.entity.UserEntity;
import site.ethosylar.pos.user.repository.UserRepository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    private final AuditActionRepository
            auditActionRepository;

    private final ModuleRepository moduleRepository;

    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    @Transactional
    public void log(AuditLogRequest request) {

        AuditActionEntity action =
                auditActionRepository
                    .findByCodeIgnoreCase(
                        request.getActionCode()
                    )
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Audit action not found: "
                            + request.getActionCode()
                        )
                    );

        ModuleEntity module =
                resolveModule(
                    request.getModuleCode()
                );

        UserEntity user =
                resolveActor(
                    request.getActorUserId()
                );

        HttpServletRequest httpRequest =
                currentHttpRequest();

        AuditLogEntity audit =
                new AuditLogEntity();

        audit.setUser(user);
        audit.setAction(action);
        audit.setModule(module);

        audit.setEntityType(
            request.getEntityType()
        );

        audit.setEntityId(
            request.getEntityId()
        );

        audit.setDescription(
            request.getDescription()
        );

        audit.setBeforeData(
            toJson(request.getBeforeData())
        );

        audit.setAfterData(
            toJson(request.getAfterData())
        );

        if (httpRequest != null) {

            audit.setIpAddress(
                httpRequest.getRemoteAddr()
            );

            audit.setUserAgent(
                truncate(
                    httpRequest.getHeader(
                        "User-Agent"
                    ),
                    500
                )
            );
        }

        auditLogRepository.save(audit);
    }

    private UserEntity resolveActor(
            Long actorUserId) {

        if (actorUserId != null) {

            return userRepository
                    .findById(actorUserId)
                    .orElse(null);
        }

        Authentication authentication =
                SecurityContextHolder
                    .getContext()
                    .getAuthentication();

        if (authentication == null
                || !authentication
                    .isAuthenticated()
                || authentication
                    instanceof AnonymousAuthenticationToken) {

            return null;
        }

        return userRepository
                .findByUsernameIgnoreCase(
                    authentication.getName()
                )
                .orElse(null);
    }

    private ModuleEntity resolveModule(
            String moduleCode) {

        if (moduleCode == null
                || moduleCode.isBlank()) {

            return null;
        }

        return moduleRepository
                .findByCodeIgnoreCase(
                    moduleCode
                )
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Module not found: "
                        + moduleCode
                    )
                );
    }

    private JsonNode toJson(Object value) {

        if (value == null) {
            return null;
        }

        return objectMapper.valueToTree(value);
    }

    private HttpServletRequest
            currentHttpRequest() {

        if (!(RequestContextHolder
                .getRequestAttributes()
                instanceof ServletRequestAttributes attributes)) {

            return null;
        }

        return attributes.getRequest();
    }

    private String truncate(
            String value,
            int maxLength) {

        if (value == null) {
            return null;
        }

        if (value.length() <= maxLength) {
            return value;
        }

        return value.substring(
                0,
                maxLength
        );
    }
}