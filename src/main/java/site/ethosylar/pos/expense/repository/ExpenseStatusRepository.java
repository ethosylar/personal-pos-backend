package site.ethosylar.pos.expense.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.expense.entity.ExpenseStatusEntity;

public interface ExpenseStatusRepository extends JpaRepository<ExpenseStatusEntity, Long> {

	Optional<ExpenseStatusEntity> findByCodeIgnoreCase(String code);
}
