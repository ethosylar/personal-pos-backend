package site.ethosylar.pos.permission.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.permission.entity.ModulePermissionEntity;

public interface ModulePermissionRepository
        extends JpaRepository<ModulePermissionEntity, Long> {

    List<ModulePermissionEntity> findByModule_Id(Long moduleId);

    Optional<ModulePermissionEntity>
        findByModule_IdAndCodeIgnoreCase(
            Long moduleId,
            String code
        );
}