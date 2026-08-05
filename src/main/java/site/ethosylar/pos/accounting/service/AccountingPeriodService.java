package site.ethosylar.pos.accounting.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.ethosylar.pos.accounting.repository.MonthlyAccountingRepository;
import site.ethosylar.pos.common.exception.BusinessRuleException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountingPeriodService {

	private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Kuala_Lumpur");

	private final MonthlyAccountingRepository accountingRepository;

	public boolean isClosed(int year, int month) {

		return accountingRepository.existsByPeriodYearAndPeriodMonthAndCurrentRecordTrueAndStatus_CodeIgnoreCase(
				(short) year, (short) month, "POSTED");
	}

	public boolean isClosed(YearMonth period) {

		return isClosed(period.getYear(), period.getMonthValue());
	}

	public boolean isClosed(LocalDate date) {

		return isClosed(YearMonth.from(date));
	}

	public boolean isClosed(OffsetDateTime dateTime) {

		LocalDate businessDate = dateTime.atZoneSameInstant(BUSINESS_ZONE).toLocalDate();

		return isClosed(businessDate);
	}

	public void ensureOpen(YearMonth period) {

		if (isClosed(period)) {

			throw closedPeriodException(period);
		}
	}

	public void ensureOpen(LocalDate date) {

		ensureOpen(YearMonth.from(date));
	}

	public void ensureOpen(OffsetDateTime dateTime) {

		LocalDate businessDate = dateTime.atZoneSameInstant(BUSINESS_ZONE).toLocalDate();

		ensureOpen(businessDate);
	}

	private BusinessRuleException closedPeriodException(YearMonth period) {

		return new BusinessRuleException(
				String.format(
						"Accounting period %04d-%02d is closed. " + "Void the posted monthly accounting statement "
								+ "before making financial changes to this period.",
						period.getYear(), period.getMonthValue()));
	}
}