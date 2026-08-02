package site.ethosylar.pos.module.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.module.entity.ModuleEntity;

public interface ModuleRepository
        extends JpaRepository<ModuleEntity, Long> {

    Optional<ModuleEntity> findByCodeIgnoreCase(String code);

    List<ModuleEntity> findByActiveTrueOrderByDisplayOrderAsc();
}