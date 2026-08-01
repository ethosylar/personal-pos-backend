package site.ethosylar.pos.module.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.module.mapper.ModuleMapper;
import site.ethosylar.pos.module.repository.ModuleRepository;
import site.ethosylar.pos.module.response.ModuleResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final ModuleMapper moduleMapper;

    public List<ModuleResponse> getActiveModules() {

        return moduleRepository
                .findByActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(moduleMapper::toResponse)
                .toList();
    }
}