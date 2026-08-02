package site.ethosylar.pos.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.payment.entity.PaymentStatusEntity;

public interface PaymentStatusRepository extends JpaRepository<PaymentStatusEntity, Long> {

	Optional<PaymentStatusEntity> findByCodeIgnoreCase(String code);
}
