package site.ethosylar.pos.module.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.cache.CacheNames;
import site.ethosylar.pos.module.mapper.ModuleMapper;
import site.ethosylar.pos.module.repository.ModuleRepository;
import site.ethosylar.pos.module.response.ModuleResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModuleService {

	private final ModuleRepository moduleRepository;
	private final ModuleMapper moduleMapper;

	@Cacheable(cacheNames = CacheNames.MODULES, sync = true)
	@Transactional(readOnly = true)
	public List<ModuleResponse> getActiveModules() {

		return moduleRepository.findByActiveTrueOrderByDisplayOrderAsc().stream().map(moduleMapper::toResponse)
				.toList();
	}
}