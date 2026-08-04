package site.ethosylar.pos.staff.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.ethosylar.pos.common.entity.TimestampedEntity;
import site.ethosylar.pos.user.entity.UserEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dt_staff_salary_rates_ssr")
public class StaffSalaryRateEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "staff_id", nullable = false)
	private StaffEntity staff;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pay_type_id", nullable = false)
	private SalaryPayTypeEntity payType;

	@Column(name = "base_rate", nullable = false, precision = 18, scale = 4)
	private BigDecimal baseRate;

	@Column(name = "fixed_allowance", nullable = false, precision = 18, scale = 4)
	private BigDecimal fixedAllowance = BigDecimal.ZERO;

	@Column(name = "currency_code", nullable = false, length = 3)
	private String currencyCode = "MYR";

	@Column(name = "effective_from", nullable = false)
	private LocalDate effectiveFrom;

	@Column(name = "effective_to")
	private LocalDate effectiveTo;

	@Column(name = "notes", length = 1000)
	private String notes;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by_user_id")
	private UserEntity createdByUser;
}