package site.ethosylar.pos.category.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import site.ethosylar.pos.category.entity.ProductCategoryEntity;

public interface ProductCategoryRepository
        extends JpaRepository<ProductCategoryEntity, Long>,
                JpaSpecificationExecutor<ProductCategoryEntity> {

    Optional<ProductCategoryEntity>
        findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);
}