package site.ethosylar.pos.expense.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseNumberService {

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

	private final JdbcTemplate jdbcTemplate;

	public String nextExpenseNumber() {

		Long sequence = jdbcTemplate.queryForObject("SELECT nextval('seq_expense_number')", Long.class);

		return String.format("EXP-%s-%06d", LocalDate.now().format(DATE_FORMAT), sequence);
	}
}