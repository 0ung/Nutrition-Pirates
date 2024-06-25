package codehows.dream.nutritionpirates.dto;

import codehows.dream.nutritionpirates.constants.Process;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkPlanListDTO {
	private Long work_plan_id;
	private Process process;
	private String lotCode;
	private String startTime;
	private String endTime;
	private String worker;
}
