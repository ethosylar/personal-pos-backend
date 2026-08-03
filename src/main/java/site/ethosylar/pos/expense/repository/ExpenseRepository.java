package site.ethosylar.pos.expense.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import site.ethosylar.pos.expense.entity.ExpenseEntity;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long>, JpaSpecificationExecutor<ExpenseEntity> {

	Optional<ExpenseEntity> findByExpenseNumberIgnoreCase(String expenseNumber);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			SELECT e
			FROM ExpenseEntity e
			WHERE e.id = :id
			""")
	Optional<ExpenseEntity> findByIdForUpdate(@Param("id") Long id);
}
