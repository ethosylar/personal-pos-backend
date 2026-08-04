package site.ethosylar.pos.staff.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.request.AuditLogRequest;
import site.ethosylar.pos.audit.service.AuditService;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.staff.entity.SalaryPayTypeEntity;
import site.ethosylar.pos.staff.entity.StaffEntity;
import site.ethosylar.pos.staff.entity.StaffSalaryRateEntity;
import site.ethosylar.pos.staff.mapper.StaffMapper;
import site.ethosylar.pos.staff.repository.SalaryPayTypeRepository;
import site.ethosylar.pos.staff.repository.StaffRepository;
import site.ethosylar.pos.staff.repository.StaffSalaryRateRepository;
import site.ethosylar.pos.staff.request.CreateSalaryRateRequest;
import site.ethosylar.pos.staff.response.SalaryRateResponse;
import site.ethosylar.pos.user.entity.UserEntity;
import site.ethosylar.pos.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffSalaryService {

	private final StaffRepository staffRepository;

	private final StaffSalaryRateRepository salaryRateRepository;

	private final SalaryPayTypeRepository payTypeRepository;

	private final UserRepository userRepository;

	private final StaffMapper staffMapper;

	private final AuditService auditService;

	@Transactional(readOnly = true)
	public List<SalaryRateResponse> getHistory(Long staffId) {

		ensureStaffExists(staffId);

		return salaryRateRepository.findByStaff_IdOrderByEffectiveFromDesc(staffId).stream()
				.map(staffMapper::toSalaryRateResponse).toList();
	}

	public SalaryRateResponse addRate(Long staffId, CreateSalaryRateRequest request) {

		StaffEntity staff = staffRepository.findByIdForUpdate(staffId)
				.orElseThrow(() -> new ResourceNotFoundException("Staff not found."));

		if ("TERMINATED".equalsIgnoreCase(staff.getStatus().getCode())) {

			throw new BusinessRuleException("Cannot add a salary rate to terminated staff.");
		}

		SalaryPayTypeEntity payType = payTypeRepository.findById(request.getPayTypeId())
				.orElseThrow(() -> new ResourceNotFoundException("Salary pay type not found."));

		if (!Boolean.TRUE.equals(payType.getActive())) {

			throw new BusinessRuleException("Salary pay type is inactive.");
		}

		StaffSalaryRateEntity currentRate = salaryRateRepository.findFirstByStaff_IdAndEffectiveToIsNull(staffId)
				.orElse(null);

		if (currentRate != null) {

			/*
			 * For the MVP, salary changes must move forward through time.
			 */
			if (!request.getEffectiveFrom().isAfter(currentRate.getEffectiveFrom())) {

				throw new BusinessRuleException(
						"New salary effective date must be after the current salary rate effective date.");
			}

			currentRate.setEffectiveTo(request.getEffectiveFrom().minusDays(1));

			salaryRateRepository.save(currentRate);
		}

		StaffSalaryRateEntity rate = new StaffSalaryRateEntity();

		rate.setStaff(staff);

		rate.setPayType(payType);

		rate.setBaseRate(request.getBaseRate());

		rate.setFixedAllowance(request.getFixedAllowance());

		rate.setCurrencyCode("MYR");

		rate.setEffectiveFrom(request.getEffectiveFrom());

		rate.setEffectiveTo(null);

		rate.setNotes(normalize(request.getNotes()));

		rate.setCreatedByUser(getCurrentUser());

		rate = salaryRateRepository.save(rate);

		auditService.log(AuditLogRequest.builder().actionCode("CREATE").moduleCode("STAFF")
				.entityType("STAFF_SALARY_RATE").entityId(rate.getId()).description("Staff salary rate created.")
				.afterData(snapshot(rate)).build());

		return staffMapper.toSalaryRateResponse(rate);
	}

	private void ensureStaffExists(Long staffId) {

		if (!staffRepository.existsById(staffId)) {

			throw new ResourceNotFoundException("Staff not found.");
		}
	}

	private UserEntity getCurrentUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {

			return null;
		}

		return userRepository.findByUsernameIgnoreCase(authentication.getName()).orElse(null);
	}

	private String normalize(String value) {

		if (value == null || value.isBlank()) {

			return null;
		}

		return value.trim();
	}

	private Map<String, Object> snapshot(StaffSalaryRateEntity rate) {

		Map<String, Object> data = new LinkedHashMap<>();

		data.put("staffId", rate.getStaff().getId());

		data.put("payType", rate.getPayType().getCode());

		data.put("baseRate", rate.getBaseRate());

		data.put("fixedAllowance", rate.getFixedAllowance());

		data.put("currencyCode", rate.getCurrencyCode());

		data.put("effectiveFrom", rate.getEffectiveFrom());

		data.put("effectiveTo", rate.getEffectiveTo());

		return data;
	}

}
