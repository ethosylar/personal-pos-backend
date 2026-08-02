package site.ethosylar.pos.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.payment.entity.PaymentMethodEntity;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, Long> {

	Optional<PaymentMethodEntity> findByCodeIgnoreCase(String code);

	List<PaymentMethodEntity> findByActiveTrueOrderByDisplayOrderAscNameAsc();
}