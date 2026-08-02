package site.ethosylar.pos.sale.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.request.AuditLogRequest;
import site.ethosylar.pos.audit.service.AuditService;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.DuplicateResourceException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.payment.repository.PaymentRepository;
import site.ethosylar.pos.payment.response.PaymentResponse;
import site.ethosylar.pos.product.entity.ProductEntity;
import site.ethosylar.pos.product.repository.ProductRepository;
import site.ethosylar.pos.sale.entity.SaleEntity;
import site.ethosylar.pos.sale.entity.SaleItemEntity;
import site.ethosylar.pos.sale.entity.SaleStatusEntity;
import site.ethosylar.pos.sale.mapper.SaleMapper;
import site.ethosylar.pos.sale.repository.SaleItemRepository;
import site.ethosylar.pos.sale.repository.SaleRepository;
import site.ethosylar.pos.sale.repository.SaleStatusRepository;
import site.ethosylar.pos.sale.request.CreateSaleItemRequest;
import site.ethosylar.pos.sale.request.CreateSaleRequest;
import site.ethosylar.pos.sale.request.UpdateSaleItemRequest;
import site.ethosylar.pos.sale.request.UpdateSaleRequest;
import site.ethosylar.pos.sale.response.SaleItemResponse;
import site.ethosylar.pos.sale.response.SaleResponse;
import site.ethosylar.pos.user.entity.UserEntity;
import site.ethosylar.pos.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleService {

	private final SaleRepository saleRepository;
	private final SaleItemRepository itemRepository;
	private final SaleStatusRepository statusRepository;

	private final ProductRepository productRepository;

	private final PaymentRepository paymentRepository;

	private final UserRepository userRepository;

	private final SaleNumberService numberService;
	private final SaleMapper saleMapper;

	private final AuditService auditService;

	public SaleResponse create(CreateSaleRequest request) {

		SaleEntity sale = new SaleEntity();

		sale.setSaleNumber(numberService.nextSaleNumber());

		sale.setStatus(getStatus("DRAFT"));

		sale.setCashierUser(getCurrentUser());

		sale.setSubtotal(BigDecimal.ZERO);

		sale.setDiscountAmount(request.getDiscountAmount());

		sale.setTaxAmount(request.getTaxAmount());

		sale.setTotalAmount(request.getTaxAmount().subtract(request.getDiscountAmount()).max(BigDecimal.ZERO));

		sale.setNotes(normalize(request.getNotes()));

		sale = saleRepository.save(sale);

		auditService.log(AuditLogRequest.builder().actionCode("CREATE").moduleCode("POS").entityType("SALE")
				.entityId(sale.getId()).description("Draft sale created.").build());

		return buildResponse(sale);
	}

	public SaleResponse addItem(Long saleId, CreateSaleItemRequest request) {

		SaleEntity sale = getDraftSaleForUpdate(saleId);

		ProductEntity product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new ResourceNotFoundException("Product not found."));

		if (!Boolean.TRUE.equals(product.getActive())) {

			throw new BusinessRuleException("Product is inactive.");
		}

		if (!Boolean.TRUE.equals(product.getAvailable())) {

			throw new BusinessRuleException("Product is currently unavailable.");
		}

		if (itemRepository.existsBySale_IdAndProduct_Id(saleId, product.getId())) {

			throw new DuplicateResourceException(
					"Product is already included in this sale. Update the existing sale item quantity instead.");
		}

		BigDecimal lineSubtotal = product.getSellingPrice().multiply(request.getQuantity()).setScale(4,
				RoundingMode.HALF_UP);

		if (request.getDiscountAmount().compareTo(lineSubtotal) > 0) {

			throw new BusinessRuleException("Item discount cannot exceed the item subtotal.");
		}

		SaleItemEntity item = new SaleItemEntity();

		item.setSale(sale);
		item.setProduct(product);

		/*
		 * Snapshot customer-facing product details now.
		 */
		item.setProductCodeSnapshot(product.getCode());

		item.setProductNameSnapshot(product.getName());

		item.setQuantity(request.getQuantity());

		item.setUnitPrice(product.getSellingPrice());

		item.setLineSubtotal(lineSubtotal);

		item.setDiscountAmount(request.getDiscountAmount());

		item.setLineTotal(lineSubtotal.subtract(request.getDiscountAmount()));

		item.setNotes(normalize(request.getNotes()));

		itemRepository.save(item);

		recalculateSale(sale);

		return buildResponse(sale);
	}

	private void recalculateSale(SaleEntity sale) {

		List<SaleItemEntity> items = itemRepository.findBySale_IdOrderByIdAsc(sale.getId());

		BigDecimal subtotal = items.stream().map(SaleItemEntity::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add)
				.setScale(4, RoundingMode.HALF_UP);

		if (sale.getDiscountAmount().compareTo(subtotal) > 0) {

			throw new BusinessRuleException("Sale discount cannot exceed subtotal.");
		}

		BigDecimal total = subtotal.subtract(sale.getDiscountAmount()).add(sale.getTaxAmount()).setScale(4,
				RoundingMode.HALF_UP);

		sale.setSubtotal(subtotal);
		sale.setTotalAmount(total);

		saleRepository.save(sale);
	}

	private SaleEntity getDraftSaleForUpdate(Long id) {

		SaleEntity sale = saleRepository.findByIdForUpdate(id)
				.orElseThrow(() -> new ResourceNotFoundException("Sale not found."));

		if (!"DRAFT".equalsIgnoreCase(sale.getStatus().getCode())) {

			throw new BusinessRuleException("Only draft sales can be modified.");
		}

		return sale;
	}

	private String normalize(String value) {

		if (value == null || value.isBlank()) {

			return null;
		}

		return value.trim();
	}

	private UserEntity getCurrentUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {

			return null;
		}

		return userRepository.findByUsernameIgnoreCase(authentication.getName()).orElse(null);
	}

	private SaleStatusEntity getStatus(String code) {

		return statusRepository.findByCodeIgnoreCase(code)
				.orElseThrow(() -> new ResourceNotFoundException("Sale status not found: " + code));
	}

	private SaleResponse buildResponse(SaleEntity sale) {

		List<SaleItemResponse> items = itemRepository.findBySale_IdOrderByIdAsc(sale.getId()).stream()
				.map(saleMapper::toItemResponse).toList();

		List<PaymentResponse> payments = paymentRepository.findBySale_IdOrderByCreatedAtAsc(sale.getId()).stream()
				.map(saleMapper::toPaymentResponse).toList();

		return saleMapper.toResponse(sale, items, payments);
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public SaleResponse getById(Long id) {

		SaleEntity sale = saleRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Sale not found."));

		return buildResponse(sale);
	}

	public SaleResponse update(Long saleId, UpdateSaleRequest request) {

		SaleEntity sale = getDraftSaleForUpdate(saleId);

		sale.setDiscountAmount(request.getDiscountAmount());

		sale.setTaxAmount(request.getTaxAmount());

		sale.setNotes(normalize(request.getNotes()));

		recalculateSale(sale);

		return buildResponse(sale);
	}

	public SaleResponse updateItem(Long saleId, Long itemId, UpdateSaleItemRequest request) {

		SaleEntity sale = getDraftSaleForUpdate(saleId);

		SaleItemEntity item = itemRepository.findByIdAndSale_Id(itemId, saleId)
				.orElseThrow(() -> new ResourceNotFoundException("Sale item not found."));

		BigDecimal lineSubtotal = item.getUnitPrice().multiply(request.getQuantity()).setScale(4, RoundingMode.HALF_UP);

		if (request.getDiscountAmount().compareTo(lineSubtotal) > 0) {

			throw new BusinessRuleException("Item discount cannot exceed item subtotal.");
		}

		item.setQuantity(request.getQuantity());

		item.setLineSubtotal(lineSubtotal);

		item.setDiscountAmount(request.getDiscountAmount());

		item.setLineTotal(lineSubtotal.subtract(request.getDiscountAmount()));

		item.setNotes(normalize(request.getNotes()));

		itemRepository.save(item);

		recalculateSale(sale);

		return buildResponse(sale);
	}

	public SaleResponse deleteItem(Long saleId, Long itemId) {

		SaleEntity sale = getDraftSaleForUpdate(saleId);

		SaleItemEntity item = itemRepository.findByIdAndSale_Id(itemId, saleId)
				.orElseThrow(() -> new ResourceNotFoundException("Sale item not found."));

		itemRepository.delete(item);

		itemRepository.flush();

		recalculateSale(sale);

		return buildResponse(sale);
	}
}
