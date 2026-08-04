package site.ethosylar.pos.staff.mapper;

import org.springframework.stereotype.Component;

import site.ethosylar.pos.staff.entity.SalaryPayTypeEntity;
import site.ethosylar.pos.staff.entity.StaffEntity;
import site.ethosylar.pos.staff.entity.StaffSalaryRateEntity;
import site.ethosylar.pos.staff.entity.StaffStatusEntity;
import site.ethosylar.pos.staff.response.SalaryRateResponse;
import site.ethosylar.pos.staff.response.StaffLookupResponse;
import site.ethosylar.pos.staff.response.StaffResponse;
import site.ethosylar.pos.staff.response.StaffSummaryResponse;

@Component
public class StaffMapper {

	public StaffSummaryResponse toSummaryResponse(StaffEntity staff) {

		return StaffSummaryResponse.builder().id(staff.getId()).staffCode(staff.getStaffCode())
				.fullName(staff.getFullName()).email(staff.getEmail()).phone(staff.getPhone())

				.statusCode(staff.getStatus().getCode())

				.statusName(staff.getStatus().getName())

				.hireDate(staff.getHireDate())

				.terminationDate(staff.getTerminationDate())

				.userId(staff.getUser() != null ? staff.getUser().getId() : null)

				.username(staff.getUser() != null ? staff.getUser().getUsername() : null)

				.build();
	}

	public StaffResponse toResponse(StaffEntity staff, SalaryRateResponse currentSalaryRate) {

		return StaffResponse.builder().id(staff.getId()).staffCode(staff.getStaffCode()).fullName(staff.getFullName())
				.email(staff.getEmail()).phone(staff.getPhone())

				.userId(staff.getUser() != null ? staff.getUser().getId() : null)

				.username(staff.getUser() != null ? staff.getUser().getUsername() : null)

				.statusId(staff.getStatus().getId())

				.statusCode(staff.getStatus().getCode())

				.statusName(staff.getStatus().getName())

				.hireDate(staff.getHireDate())

				.terminationDate(staff.getTerminationDate())

				.notes(staff.getNotes())

				.currentSalaryRate(currentSalaryRate)

				.createdAt(staff.getCreatedAt())

				.updatedAt(staff.getUpdatedAt())

				.build();
	}

	public SalaryRateResponse toSalaryRateResponse(StaffSalaryRateEntity rate) {

		return SalaryRateResponse.builder().id(rate.getId())

				.staffId(rate.getStaff().getId())

				.payTypeId(rate.getPayType().getId())

				.payTypeCode(rate.getPayType().getCode())

				.payTypeName(rate.getPayType().getName())

				.baseRate(rate.getBaseRate())

				.fixedAllowance(rate.getFixedAllowance())

				.currencyCode(rate.getCurrencyCode())

				.effectiveFrom(rate.getEffectiveFrom())

				.effectiveTo(rate.getEffectiveTo())

				.current(rate.getEffectiveTo() == null)

				.notes(rate.getNotes())

				.createdByUserId(rate.getCreatedByUser() != null ? rate.getCreatedByUser().getId() : null)

				.createdByUsername(rate.getCreatedByUser() != null ? rate.getCreatedByUser().getUsername() : null)

				.createdAt(rate.getCreatedAt())

				.build();
	}

	public StaffLookupResponse toStatusResponse(StaffStatusEntity status) {

		return StaffLookupResponse.builder().id(status.getId()).code(status.getCode()).name(status.getName())
				.description(status.getDescription()).build();
	}

	public StaffLookupResponse toPayTypeResponse(SalaryPayTypeEntity type) {

		return StaffLookupResponse.builder().id(type.getId()).code(type.getCode()).name(type.getName())
				.description(type.getDescription()).build();
	}
}