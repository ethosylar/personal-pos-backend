package site.ethosylar.pos.accounting.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonthlyAccountingNumberService {

	private final JdbcTemplate jdbcTemplate;

	public String nextNumber(int year, int month) {

		Long sequence = jdbcTemplate.queryForObject("""
				SELECT nextval(
				    'seq_monthly_accounting_number'
				)
				""", Long.class);

		return String.format("ACC-%04d%02d-%06d", year, month, sequence);
	}
}