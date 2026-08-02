package site.ethosylar.pos.sale.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import site.ethosylar.pos.payment.entity.PaymentEntity;
import site.ethosylar.pos.payment.response.PaymentResponse;
import site.ethosylar.pos.sale.entity.SaleEntity;
import site.ethosylar.pos.sale.entity.SaleItemEntity;
import site.ethosylar.pos.sale.response.SaleItemResponse;
import site.ethosylar.pos.sale.response.SaleResponse;

@Component
public class SaleMapper {

	public SaleItemResponse toItemResponse(SaleItemEntity item) {

		return SaleItemResponse.builder().id(item.getId())

				.productId(item.getProduct().getId())

				.productCode(item.getProductCodeSnapshot())

				.productName(item.getProductNameSnapshot())

				.recipeId(item.getRecipe() != null ? item.getRecipe().getId() : null)

				.recipeVersionNo(item.getRecipeVersionNo())

				.quantity(item.getQuantity())

				.unitPrice(item.getUnitPrice())

				.lineSubtotal(item.getLineSubtotal())

				.discountAmount(item.getDiscountAmount())

				.lineTotal(item.getLineTotal())

				.unitCostSnapshot(item.getUnitCostSnapshot())

				.lineCostSnapshot(item.getLineCostSnapshot())

				.grossProfitSnapshot(item.getGrossProfitSnapshot())

				.notes(item.getNotes())

				.build();
	}

	public PaymentResponse toPaymentResponse(PaymentEntity payment) {

		return PaymentResponse.builder().id(payment.getId())

				.paymentMethodId(payment.getPaymentMethod().getId())

				.paymentMethodCode(payment.getPaymentMethod().getCode())

				.paymentMethodName(payment.getPaymentMethod().getName())

				.paymentStatusCode(payment.getPaymentStatus().getCode())

				.amount(payment.getAmount())

				.tenderedAmount(payment.getTenderedAmount())

				.changeAmount(payment.getChangeAmount())

				.referenceNo(payment.getReferenceNo())

				.paidAt(payment.getPaidAt())

				.build();
	}

	public SaleResponse toResponse(SaleEntity sale, List<SaleItemResponse> items, List<PaymentResponse> payments) {

		return SaleResponse.builder().id(sale.getId())

				.saleNumber(sale.getSaleNumber())

				.statusCode(sale.getStatus().getCode())

				.statusName(sale.getStatus().getName())

				.cashierUserId(sale.getCashierUser() != null ? sale.getCashierUser().getId() : null)

				.cashierUsername(sale.getCashierUser() != null ? sale.getCashierUser().getUsername() : null)

				.soldAt(sale.getSoldAt())

				.subtotal(sale.getSubtotal())

				.discountAmount(sale.getDiscountAmount())

				.taxAmount(sale.getTaxAmount())

				.totalAmount(sale.getTotalAmount())

				.totalCostSnapshot(sale.getTotalCostSnapshot())

				.grossProfitSnapshot(sale.getGrossProfitSnapshot())

				.notes(sale.getNotes())

				.items(items)

				.payments(payments)

				.createdAt(sale.getCreatedAt())

				.updatedAt(sale.getUpdatedAt())

				.build();
	}
}