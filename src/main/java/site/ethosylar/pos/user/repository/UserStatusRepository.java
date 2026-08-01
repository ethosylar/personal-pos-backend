package site.ethosylar.pos.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.user.entity.UserStatusEntity;

public interface UserStatusRepository
        extends JpaRepository<UserStatusEntity, Long> {

    Optional<UserStatusEntity> findByCodeIgnoreCase(String code);
}