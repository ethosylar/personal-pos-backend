package site.ethosylar.pos.unit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.unit.entity.UnitOfMeasureEntity;

public interface UnitOfMeasureRepository
        extends JpaRepository<UnitOfMeasureEntity, Long> {

    Optional<UnitOfMeasureEntity>
        findByCodeIgnoreCase(String code);

    List<UnitOfMeasureEntity>
        findByActiveTrueOrderByDisplayOrderAscNameAsc();
}