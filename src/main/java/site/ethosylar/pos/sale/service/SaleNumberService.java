package site.ethosylar.pos.sale.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SaleNumberService {

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

	private final JdbcTemplate jdbcTemplate;

	public String nextSaleNumber() {

		Long sequence = jdbcTemplate.queryForObject("SELECT nextval('seq_sale_number')", Long.class);

		return String.format("SAL-%s-%06d", LocalDate.now().format(DATE_FORMAT), sequence);
	}
}