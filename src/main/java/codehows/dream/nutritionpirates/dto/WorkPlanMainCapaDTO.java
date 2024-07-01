package codehows.dream.nutritionpirates.dto;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkPlanMainCapaDTO {
	private Long workPlanId;
	private String rawsCode;
	private Facility facility;
	private Process process;
	private int capa;

	public static WorkPlanMainCapaDTO toWorkPlanMainCapaDTO(WorkPlan plan) {
		return WorkPlanMainCapaDTO.builder()
			.workPlanId(plan.getId())
			.rawsCode(plan.getRawsCodes())
			.facility(plan.getFacility())
			.capa(plan.getCapacity())
			.process(plan.getProcess())
			.build();
	}
}
