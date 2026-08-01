package site.ethosylar.pos.role.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.role.entity.RoleModuleEntity;

public interface RoleModuleRepository
        extends JpaRepository<RoleModuleEntity, Long> {

    List<RoleModuleEntity> findByRole_Id(Long roleId);
    
    Optional<RoleModuleEntity>
    findByRole_IdAndModule_Id(
        Long roleId,
        Long moduleId
    );

    boolean existsByRole_IdAndModule_Id(
            Long roleId,
            Long moduleId
    );
}