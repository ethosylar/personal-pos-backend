package site.ethosylar.pos.supplier.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import site.ethosylar.pos.supplier.entity.SupplierEntity;

public interface SupplierRepository
        extends JpaRepository<SupplierEntity, Long>,
                JpaSpecificationExecutor<SupplierEntity> {

    Optional<SupplierEntity>
        findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);
}