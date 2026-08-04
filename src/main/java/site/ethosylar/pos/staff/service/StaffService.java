package site.ethosylar.pos.staff.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.audit.request.AuditLogRequest;
import site.ethosylar.pos.audit.service.AuditService;
import site.ethosylar.pos.common.exception.BusinessRuleException;
import site.ethosylar.pos.common.exception.DuplicateResourceException;
import site.ethosylar.pos.common.exception.ResourceNotFoundException;
import site.ethosylar.pos.staff.entity.StaffEntity;
import site.ethosylar.pos.staff.entity.StaffStatusEntity;
import site.ethosylar.pos.staff.mapper.StaffMapper;
import site.ethosylar.pos.staff.repository.StaffRepository;
import site.ethosylar.pos.staff.repository.StaffSalaryRateRepository;
import site.ethosylar.pos.staff.repository.StaffStatusRepository;
import site.ethosylar.pos.staff.request.CreateStaffRequest;
import site.ethosylar.pos.staff.request.UpdateStaffRequest;
import site.ethosylar.pos.staff.response.SalaryRateResponse;
import site.ethosylar.pos.staff.response.StaffResponse;
import site.ethosylar.pos.user.entity.UserEntity;
import site.ethosylar.pos.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffService {

	private final StaffRepository staffRepository;

	private final StaffStatusRepository statusRepository;

	private final StaffSalaryRateRepository salaryRateRepository;

	private final UserRepository userRepository;

	private final StaffMapper staffMapper;

	private final AuditService auditService;

	public StaffResponse create(CreateStaffRequest request) {

		String staffCode = request.getStaffCode().trim().toUpperCase();

		if (staffRepository.existsByStaffCodeIgnoreCase(staffCode)) {

			throw new DuplicateResourceException("Staff code already exists.");
		}

		UserEntity user = resolveUser(request.getUserId(), null);

		StaffEntity staff = new StaffEntity();

		staff.setStaffCode(staffCode);

		staff.setFullName(request.getFullName().trim());

		staff.setEmail(normalize(request.getEmail()));

		staff.setPhone(normalize(request.getPhone()));

		staff.setUser(user);

		staff.setStatus(getStatus("ACTIVE"));

		staff.setHireDate(request.getHireDate());

		staff.setNotes(normalize(request.getNotes()));

		staff = staffRepository.save(staff);

		auditService.log(AuditLogRequest.builder().actionCode("CREATE").moduleCode("STAFF").entityType("STAFF")
				.entityId(staff.getId()).description("Staff record created.").afterData(snapshot(staff)).build());

		return buildResponse(staff);
	}

	@Transactional(readOnly = true)
	public StaffResponse getById(Long id) {

		StaffEntity staff = staffRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Staff not found."));

		return buildResponse(staff);
	}

	public StaffResponse update(Long id, UpdateStaffRequest request) {

		StaffEntity staff = staffRepository.findByIdForUpdate(id)
				.orElseThrow(() -> new ResourceNotFoundException("Staff not found."));

		String staffCode = request.getStaffCode().trim().toUpperCase();

		if (staffRepository.existsByStaffCodeIgnoreCaseAndIdNot(staffCode, id)) {

			throw new DuplicateResourceException("Staff code already exists.");
		}

		if (request.getTerminationDate() != null && request.getTerminationDate().isBefore(request.getHireDate())) {

			throw new BusinessRuleException("Termination date cannot be before hire date.");
		}

		StaffStatusEntity status = statusRepository.findById(request.getStatusId())
				.orElseThrow(() -> new ResourceNotFoundException("Staff status not found."));

		if (!Boolean.TRUE.equals(status.getActive())) {

			throw new BusinessRuleException("Staff status is inactive.");
		}

		if ("TERMINATED".equalsIgnoreCase(status.getCode()) && request.getTerminationDate() == null) {

			throw new BusinessRuleException("Termination date is required when staff status is TERMINATED.");
		}

		if (!"TERMINATED".equalsIgnoreCase(status.getCode()) && request.getTerminationDate() != null) {

			throw new BusinessRuleException("Termination date can only be set for terminated staff.");
		}

		Map<String, Object> before = snapshot(staff);

		staff.setStaffCode(staffCode);

		staff.setFullName(request.getFullName().trim());

		staff.setEmail(normalize(request.getEmail()));

		staff.setPhone(normalize(request.getPhone()));

		staff.setUser(resolveUser(request.getUserId(), staff.getId()));

		staff.setStatus(status);

		staff.setHireDate(request.getHireDate());

		staff.setTerminationDate(request.getTerminationDate());

		staff.setNotes(normalize(request.getNotes()));

		staffRepository.save(staff);

		auditService.log(AuditLogRequest.builder().actionCode("UPDATE").moduleCode("STAFF").entityType("STAFF")
				.entityId(staff.getId()).description("Staff record updated.").beforeData(before)
				.afterData(snapshot(staff)).build());

		return buildResponse(staff);
	}

	private StaffStatusEntity getStatus(String code) {

		return statusRepository.findByCodeIgnoreCase(code)
				.orElseThrow(() -> new ResourceNotFoundException("Staff status not found: " + code));
	}

	private UserEntity resolveUser(Long userId, Long currentStaffId) {

		if (userId == null) {
			return null;
		}

		boolean alreadyLinked;

		if (currentStaffId == null) {

			alreadyLinked = staffRepository.existsByUser_Id(userId);

		} else {

			alreadyLinked = staffRepository.existsByUser_IdAndIdNot(userId, currentStaffId);
		}

		if (alreadyLinked) {

			throw new BusinessRuleException("This user is already linked to another staff record.");
		}

		return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found."));
	}

	private StaffResponse buildResponse(StaffEntity staff) {

		SalaryRateResponse currentRate = salaryRateRepository.findFirstByStaff_IdAndEffectiveToIsNull(staff.getId())
				.map(staffMapper::toSalaryRateResponse).orElse(null);

		return staffMapper.toResponse(staff, currentRate);
	}

	private String normalize(String value) {

		if (value == null || value.isBlank()) {

			return null;
		}

		return value.trim();
	}

	private Map<String, Object> snapshot(StaffEntity staff) {

		Map<String, Object> data = new LinkedHashMap<>();

		data.put("staffCode", staff.getStaffCode());

		data.put("fullName", staff.getFullName());

		data.put("status", staff.getStatus().getCode());

		data.put("userId", staff.getUser() != null ? staff.getUser().getId() : null);

		data.put("hireDate", staff.getHireDate());

		data.put("terminationDate", staff.getTerminationDate());

		return data;
	}

}