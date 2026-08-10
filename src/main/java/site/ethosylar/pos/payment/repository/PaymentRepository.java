package site.ethosylar.pos.payment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.payment.entity.PaymentEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

	List<PaymentEntity> findBySale_IdOrderByCreatedAtAsc(Long saleId);

	@EntityGraph(attributePaths = { "status", "method" })
	List<PaymentEntity> findBySale_IdOrderByIdAsc(Long saleId);
}