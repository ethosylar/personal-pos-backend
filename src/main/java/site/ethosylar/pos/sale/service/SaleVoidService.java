package site.ethosylar.pos.sale.service;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.accounting.service.AccountingPeriodService;
import site.ethosylar.pos.audit.request.AuditLogRequest;
import site.ethosylar.pos.audit.service.AuditService;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.inventory.entity.InventoryMovementEntity;
import site.ethosylar.pos.inventory.repository.InventoryMovementRepository;
import site.ethosylar.pos.inventory.service.InventoryPostingService;
import site.ethosylar.pos.payment.entity.PaymentEntity;
import site.ethosylar.pos.payment.entity.PaymentStatusEntity;
import site.ethosylar.pos.payment.repository.PaymentRepository;
import site.ethosylar.pos.payment.repository.PaymentStatusRepository;
import site.ethosylar.pos.payment.response.PaymentResponse;
import site.ethosylar.pos.sale.entity.SaleEntity;
import site.ethosylar.pos.sale.entity.SaleItemEntity;
import site.ethosylar.pos.sale.entity.SaleStatusEntity;
import site.ethosylar.pos.sale.mapper.SaleMapper;
import site.ethosylar.pos.sale.repository.SaleItemRepository;
import site.ethosylar.pos.sale.repository.SaleRepository;
import site.ethosylar.pos.sale.repository.SaleStatusRepository;
import site.ethosylar.pos.sale.request.VoidSaleRequest;
import site.ethosylar.pos.sale.response.SaleItemResponse;
import site.ethosylar.pos.sale.response.SaleResponse;
import site.ethosylar.pos.user.entity.UserEntity;
import site.ethosylar.pos.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleVoidService {

	private final SaleRepository saleRepository;
	private final SaleItemRepository saleItemRepository;
	private final SaleStatusRepository saleStatusRepository;

	private final PaymentRepository paymentRepository;
	private final PaymentStatusRepository paymentStatusRepository;

	private final InventoryMovementRepository inventoryMovementRepository;

	private final InventoryPostingService inventoryPostingService;

	private final UserRepository userRepository;

	private final SaleMapper saleMapper;

	private final AuditService auditService;

	private final AccountingPeriodService accountingPeriodService;

	public SaleResponse voidSale(Long saleId, VoidSaleRequest request) {

		SaleEntity sale = saleRepository.findByIdForUpdate(saleId)
				.orElseThrow(() -> new ResourceNotFoundException("Sale not found."));

		if (!"COMPLETED".equalsIgnoreCase(sale.getStatus().getCode())) {

			throw new BusinessRuleException("Only completed sales can be voided.");
		}

		if (sale.getSoldAt() == null) {

			throw new BusinessRuleException("Completed sale has no sold timestamp.");
		}

		accountingPeriodService.ensureOpen(sale.getSoldAt());

		Map<String, Object> before = snapshot(sale);

		UserEntity currentUser = getCurrentUser();

		OffsetDateTime voidedAt = OffsetDateTime.now();

		List<SaleItemEntity> saleItems = saleItemRepository.findBySale_IdOrderByIdAsc(sale.getId());

		reverseInventory(sale, saleItems, currentUser, voidedAt);

		voidPayments(sale);

		sale.setStatus(getSaleStatus("VOIDED"));

		sale.setVoidedAt(voidedAt);

		sale.setVoidedByUser(currentUser);

		sale.setVoidReason(request.getReason().trim());

		saleRepository.save(sale);

		auditService.log(AuditLogRequest.builder().actionCode("STATUS_CHANGE").moduleCode("SALES").entityType("SALE")
				.entityId(sale.getId()).description("Completed sale voided.").beforeData(before)
				.afterData(snapshot(sale)).build());

		return buildResponse(sale);
	}

	private void reverseInventory(SaleEntity sale, List<SaleItemEntity> saleItems, UserEntity currentUser,
			OffsetDateTime voidedAt) {

		for (SaleItemEntity saleItem : saleItems) {

			List<InventoryMovementEntity> originalUsages = inventoryMovementRepository
					.findByReferenceTypeIgnoreCaseAndReferenceIdAndMovementType_CodeIgnoreCaseOrderByIdAsc("SALE_ITEM",
							saleItem.getId(), "SALE_USAGE");

			if (originalUsages.isEmpty()) {

				throw new BusinessRuleException("No inventory usage was found for sale item " + saleItem.getId() + ".");
			}

			for (InventoryMovementEntity original : originalUsages) {

				inventoryPostingService.recordSaleReversal(original.getIngredient().getId(),

						original.getQuantity(),

						original.getUnitCost(),

						voidedAt,

						saleItem.getId(),

						sale.getSaleNumber(),

						currentUser);
			}
		}
	}

	private void voidPayments(SaleEntity sale) {

		PaymentStatusEntity voidedStatus = paymentStatusRepository.findByCodeIgnoreCase("VOIDED")
				.orElseThrow(() -> new ResourceNotFoundException("VOIDED payment status not found."));

		List<PaymentEntity> payments = paymentRepository.findBySale_IdOrderByCreatedAtAsc(sale.getId());

		for (PaymentEntity payment : payments) {

			if ("COMPLETED".equalsIgnoreCase(payment.getPaymentStatus().getCode())) {

				payment.setPaymentStatus(voidedStatus);
			}
		}

		paymentRepository.saveAll(payments);
	}

	private SaleStatusEntity getSaleStatus(String code) {

		return saleStatusRepository.findByCodeIgnoreCase(code)
				.orElseThrow(() -> new ResourceNotFoundException("Sale status not found: " + code));
	}

	private UserEntity getCurrentUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {

			return null;
		}

		return userRepository.findByUsernameIgnoreCase(authentication.getName()).orElse(null);
	}

	private Map<String, Object> snapshot(SaleEntity sale) {

		Map<String, Object> data = new LinkedHashMap<>();

		data.put("saleNumber", sale.getSaleNumber());

		data.put("status", sale.getStatus().getCode());

		data.put("totalAmount", sale.getTotalAmount());

		data.put("totalCostSnapshot", sale.getTotalCostSnapshot());

		data.put("grossProfitSnapshot", sale.getGrossProfitSnapshot());

		data.put("soldAt", sale.getSoldAt());

		data.put("voidedAt", sale.getVoidedAt());

		data.put("voidedBy", sale.getVoidedByUser() != null ? sale.getVoidedByUser().getUsername() : null);

		data.put("voidReason", sale.getVoidReason());

		return data;
	}

	private SaleResponse buildResponse(SaleEntity sale) {

		List<SaleItemResponse> items = saleItemRepository.findBySale_IdOrderByIdAsc(sale.getId()).stream()
				.map(saleMapper::toItemResponse).toList();

		List<PaymentResponse> payments = paymentRepository.findBySale_IdOrderByCreatedAtAsc(sale.getId()).stream()
				.map(saleMapper::toPaymentResponse).toList();

		return saleMapper.toResponse(sale, items, payments);
	}
}