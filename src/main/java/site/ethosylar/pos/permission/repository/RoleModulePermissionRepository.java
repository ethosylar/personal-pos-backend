package site.ethosylar.pos.permission.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.permission.entity.RoleModulePermissionEntity;

public interface RoleModulePermissionRepository
        extends JpaRepository<RoleModulePermissionEntity, Long> {

    List<RoleModulePermissionEntity>
        findByRoleModule_Id(Long roleModuleId);

    boolean existsByRoleModule_IdAndModulePermission_Id(
        Long roleModuleId,
        Long modulePermissionId
    );
}