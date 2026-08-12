package site.ethosylar.pos.unit.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.common.cache.CacheNames;
import site.ethosylar.pos.unit.repository.UnitOfMeasureRepository;
import site.ethosylar.pos.unit.response.UnitOfMeasureResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UnitOfMeasureService {

	private final UnitOfMeasureRepository unitOfMeasureRepository;

	@Cacheable(cacheNames = CacheNames.UNITS, sync = true)
	@Transactional(readOnly = true)
	public List<UnitOfMeasureResponse> getActiveUnits() {

		return unitOfMeasureRepository.findByActiveTrueOrderByDisplayOrderAscNameAsc().stream()
				.map(unit -> UnitOfMeasureResponse.builder().id(unit.getId()).code(unit.getCode()).name(unit.getName())
						.symbol(unit.getSymbol()).measurementType(unit.getMeasurementType())
						.baseUnit(unit.getBaseUnit()).build())
				.toList();
	}
}