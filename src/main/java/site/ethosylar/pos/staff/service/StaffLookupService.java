package site.ethosylar.pos.staff.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.cache.CacheNames;
import site.ethosylar.pos.staff.mapper.StaffMapper;
import site.ethosylar.pos.staff.repository.SalaryPayTypeRepository;
import site.ethosylar.pos.staff.repository.StaffStatusRepository;
import site.ethosylar.pos.staff.response.StaffLookupResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StaffLookupService {

	private final StaffStatusRepository statusRepository;

	private final SalaryPayTypeRepository payTypeRepository;

	private final StaffMapper staffMapper;

	@Cacheable(cacheNames = CacheNames.STAFF_STATUSES, sync = true)
	@Transactional(readOnly = true)
	public List<StaffLookupResponse> getStatuses() {

		return statusRepository.findByActiveTrueOrderByDisplayOrderAscNameAsc().stream()
				.map(staffMapper::toStatusResponse).toList();
	}

	@Cacheable(cacheNames = CacheNames.SALARY_PAY_TYPES, sync = true)
	@Transactional(readOnly = true)
	public List<StaffLookupResponse> getPayTypes() {

		return payTypeRepository.findByActiveTrueOrderByDisplayOrderAscNameAsc().stream()
				.map(staffMapper::toPayTypeResponse).toList();
	}
}