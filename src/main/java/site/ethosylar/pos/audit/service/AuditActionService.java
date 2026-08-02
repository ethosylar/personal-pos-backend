package site.ethosylar.pos.audit.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.repository.AuditActionRepository;
import site.ethosylar.pos.audit.response.AuditActionLookupResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditActionService {

	private final AuditActionRepository auditActionRepository;

	public List<AuditActionLookupResponse> getActiveActions() {

		return auditActionRepository.findByActiveTrueOrderByNameAsc().stream().map(action -> AuditActionLookupResponse
				.builder().id(action.getId()).code(action.getCode()).name(action.getName()).build()).toList();
	}
}