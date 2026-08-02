package site.ethosylar.pos.procurement.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProcurementNumberService {

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

	private final JdbcTemplate jdbcTemplate;

	public String nextPurchaseOrderNumber() {

		Long sequence = jdbcTemplate.queryForObject("SELECT nextval('seq_purchase_order_number')", Long.class);

		return String.format("PO-%s-%06d", LocalDate.now().format(DATE_FORMAT), sequence);
	}

	public String nextGoodsReceiptNumber() {

		Long sequence = jdbcTemplate.queryForObject("SELECT nextval('seq_goods_receipt_number')", Long.class);

		return String.format("GR-%s-%06d", LocalDate.now().format(DATE_FORMAT), sequence);
	}
}