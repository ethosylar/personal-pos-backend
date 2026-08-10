package site.ethosylar.pos.payment.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.payment.entity.PaymentEntity;
import site.ethosylar.pos.payment.entity.PaymentMethodEntity;
import site.ethosylar.pos.payment.entity.PaymentStatusEntity;
import site.ethosylar.pos.payment.repository.PaymentMethodRepository;
import site.ethosylar.pos.payment.repository.PaymentRepository;
import site.ethosylar.pos.payment.repository.PaymentStatusRepository;
import site.ethosylar.pos.payment.request.CheckoutPaymentRequest;
import site.ethosylar.pos.sale.entity.SaleEntity;
import site.ethosylar.pos.user.entity.UserEntity;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

	private final PaymentRepository paymentRepository;

	private final PaymentMethodRepository methodRepository;

	private final PaymentStatusRepository statusRepository;

	public PaymentEntity createCompletedPayment(SaleEntity sale, CheckoutPaymentRequest request, UserEntity user,
			OffsetDateTime paidAt) {

		PaymentMethodEntity method = methodRepository.findById(request.getPaymentMethodId())
				.orElseThrow(() -> new ResourceNotFoundException("Payment method not found."));

		if (!Boolean.TRUE.equals(method.getActive())) {

			throw new BusinessRuleException("Payment method is inactive.");
		}

		PaymentStatusEntity completed = statusRepository.findByCodeIgnoreCase("COMPLETED")
				.orElseThrow(() -> new ResourceNotFoundException("COMPLETED payment status not found."));

		BigDecimal change = BigDecimal.ZERO;

		if ("CASH".equalsIgnoreCase(method.getCode())) {

			if (request.getTenderedAmount() == null) {

				throw new BusinessRuleException("Tendered amount is required for cash payments.");
			}

			if (request.getTenderedAmount().compareTo(request.getAmount()) < 0) {

				throw new BusinessRuleException("Tendered cash is less than the payment amount.");
			}

			change = request.getTenderedAmount().subtract(request.getAmount());
		}

		PaymentEntity payment = new PaymentEntity();

		payment.setSale(sale);
		payment.setPaymentMethod(method);
		payment.setPaymentStatus(completed);

		payment.setAmount(request.getAmount());

		payment.setTenderedAmount(request.getTenderedAmount());

		payment.setChangeAmount(change);

		payment.setReferenceNo(normalize(request.getReferenceNo()));

		payment.setNotes(normalize(request.getNotes()));

		payment.setPaidAt(paidAt);

		payment.setCreatedByUser(user);

		return paymentRepository.save(payment);
	}

	private String normalize(String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}
}
