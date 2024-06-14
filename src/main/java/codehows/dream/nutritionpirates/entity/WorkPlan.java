package codehows.dream.nutritionpirates.entity;

import java.sql.Date;

import codehows.dream.nutritionpirates.constants.Facility;
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
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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

	@Column(nullable = false)
	private String worker;

	private String processCompletionTime;

	@Column(nullable = false)
	private Date startTime;

	private Date endTime;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Facility facility;

	//반제품
	private String semiProduct;
}
