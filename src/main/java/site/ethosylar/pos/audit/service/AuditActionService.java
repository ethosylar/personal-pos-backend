package site.ethosylar.pos.audit.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.repository.AuditActionRepository;
import site.ethosylar.pos.audit.response.AuditActionLookupResponse;
import site.ethosylar.pos.common.cache.CacheNames;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditActionService {

	private final AuditActionRepository auditActionRepository;

	@Cacheable(cacheNames = CacheNames.AUDIT_ACTIONS, sync = true)
	@Transactional(readOnly = true)
	public List<AuditActionLookupResponse> getActiveActions() {

		return auditActionRepository.findByActiveTrueOrderByNameAsc().stream().map(action -> AuditActionLookupResponse
				.builder().id(action.getId()).code(action.getCode()).name(action.getName()).build()).toList();
	}
}