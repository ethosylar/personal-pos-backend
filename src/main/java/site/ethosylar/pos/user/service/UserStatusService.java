package site.ethosylar.pos.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.user.repository.UserStatusRepository;
import site.ethosylar.pos.user.response.UserStatusResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStatusService {

	private final UserStatusRepository userStatusRepository;

	public List<UserStatusResponse> getActiveStatuses() {

		return userStatusRepository.findByActiveTrueOrderByNameAsc().stream().map(status -> UserStatusResponse.builder()
				.id(status.getId()).code(status.getCode()).name(status.getName()).build()).toList();
	}
}