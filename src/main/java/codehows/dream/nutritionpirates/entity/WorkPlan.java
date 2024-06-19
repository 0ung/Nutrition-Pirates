package codehows.dream.nutritionpirates.entity;

import java.sql.Timestamp;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.FacilityStatus;
import codehows.dream.nutritionpirates.constants.Process;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class WorkPlan {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "work_plan_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "process_plan_id")
	private ProcessPlan processPlan;

	@Enumerated(EnumType.STRING)
	private Process process;

	private String rawsCodes;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lotCode")
	private LotCode lotCode;

	private String worker;

	private Timestamp processCompletionTime;

	private Timestamp startTime;

	private Timestamp endTime;

	@Enumerated(EnumType.STRING)
	private Facility facility;

	@Enumerated(EnumType.STRING)
	private FacilityStatus facilityStatus;

	//반제품
	private int semiProduct;

	//추가된 컬럼
	//작동 가능 여부
	private boolean activate;

}
