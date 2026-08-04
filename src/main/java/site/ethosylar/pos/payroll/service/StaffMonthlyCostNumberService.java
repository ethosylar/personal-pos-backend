package site.ethosylar.pos.payroll.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffMonthlyCostNumberService {

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

	private final JdbcTemplate jdbcTemplate;

	public String nextNumber() {

		Long sequence = jdbcTemplate.queryForObject("""
				SELECT nextval(
				    'seq_staff_monthly_cost_number'
				)
				""", Long.class);

		return String.format("PAY-%s-%06d", LocalDate.now().format(DATE_FORMAT), sequence);
	}
}