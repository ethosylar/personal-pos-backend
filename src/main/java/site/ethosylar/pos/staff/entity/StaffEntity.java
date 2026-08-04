package site.ethosylar.pos.staff.entity;

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
@Table(name = "dt_staff_stf")
public class StaffEntity extends TimestampedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "staff_code", nullable = false, unique = true, length = 50)
	private String staffCode;

	@Column(name = "full_name", nullable = false, length = 150)
	private String fullName;

	@Column(name = "email", length = 150)
	private String email;

	@Column(name = "phone", length = 50)
	private String phone;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id", nullable = false)
	private StaffStatusEntity status;

	@Column(name = "hire_date", nullable = false)
	private LocalDate hireDate;

	@Column(name = "termination_date")
	private LocalDate terminationDate;

	@Column(name = "notes", length = 1000)
	private String notes;
}