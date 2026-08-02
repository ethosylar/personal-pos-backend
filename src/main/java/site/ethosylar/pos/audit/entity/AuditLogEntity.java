package site.ethosylar.pos.audit.entity;

import java.time.OffsetDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.ethosylar.pos.module.entity.ModuleEntity;
import site.ethosylar.pos.user.entity.UserEntity;
import tools.jackson.databind.JsonNode;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dt_audit_logs_al")
public class AuditLogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "action_id", nullable = false)
	private AuditActionEntity action;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "module_id")
	private ModuleEntity module;

	@Column(name = "entity_type", nullable = false, length = 100)
	private String entityType;

	@Column(name = "entity_id")
	private Long entityId;

	@Column(name = "description", length = 1000)
	private String description;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "before_data", columnDefinition = "jsonb")
	private JsonNode beforeData;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "after_data", columnDefinition = "jsonb")
	private JsonNode afterData;

	@Column(name = "ip_address", length = 45)
	private String ipAddress;

	@Column(name = "user_agent", length = 500)
	private String userAgent;

	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	@PrePersist
	protected void onCreate() {

		if (createdAt == null) {
			createdAt = OffsetDateTime.now();
		}
	}
}