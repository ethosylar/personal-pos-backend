package site.ethosylar.pos.inventory.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.inventory.mapper.InventoryMapper;
import site.ethosylar.pos.inventory.repository.InventoryMovementTypeRepository;
import site.ethosylar.pos.inventory.response.InventoryMovementTypeResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryMovementTypeService {

	private final InventoryMovementTypeRepository repository;

	private final InventoryMapper inventoryMapper;

	public List<InventoryMovementTypeResponse> getActiveMovementTypes() {

		return repository.findByActiveTrueOrderByDisplayOrderAscNameAsc().stream()
				.map(inventoryMapper::toMovementTypeResponse).toList();
	}
}