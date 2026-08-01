package site.ethosylar.pos.role.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.role.entity.RoleEntity;

public interface RoleRepository
        extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);
}