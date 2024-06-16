package codehows.dream.nutritionpirates.entity;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

	private String processCompletionTime;

	private Date startTime;

	private Date endTime;

	@Enumerated(EnumType.STRING)
	private Facility facility;

	//반제품
	private String semiProduct;
}
