package site.ethosylar.pos.expense.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.expense.entity.ExpenseCategoryEntity;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategoryEntity, Long> {

	Optional<ExpenseCategoryEntity> findByCodeIgnoreCase(String code);

	List<ExpenseCategoryEntity> findByActiveTrueOrderByDisplayOrderAscNameAsc();
}
