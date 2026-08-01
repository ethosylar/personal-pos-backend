package site.ethosylar.pos.permission.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.module.repository.ModuleRepository;
import site.ethosylar.pos.permission.mapper.ModulePermissionMapper;
import site.ethosylar.pos.permission.repository.ModulePermissionRepository;
import site.ethosylar.pos.permission.response.ModulePermissionResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModulePermissionService {

	private final ModuleRepository moduleRepository;

	private final ModulePermissionRepository modulePermissionRepository;

	private final ModulePermissionMapper modulePermissionMapper;

	public List<ModulePermissionResponse> getActivePermissionsByModule(Long moduleId) {

		if (!moduleRepository.existsById(moduleId)) {
			throw new ResourceNotFoundException("Module not found.");
		}

		return modulePermissionRepository.findByModule_IdAndActiveTrueOrderByIdAsc(moduleId).stream()
				.map(modulePermissionMapper::toResponse).toList();
	}
}