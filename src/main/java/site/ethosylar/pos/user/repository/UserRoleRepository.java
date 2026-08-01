package site.ethosylar.pos.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.user.entity.UserRoleEntity;

public interface UserRoleRepository
        extends JpaRepository<UserRoleEntity, Long> {

    List<UserRoleEntity> findByUser_Id(Long userId);

    boolean existsByUser_IdAndRole_Id(
            Long userId,
            Long roleId
    );
}