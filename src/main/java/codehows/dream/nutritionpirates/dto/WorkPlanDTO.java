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
public class WorkPlanDTO {

	private Long id;
	private Process process;
	private String rawsCodes;
	private String lotCodes;
	private String startTime;
	private String endTime;
	private Facility facility;
	private boolean activate;

	public static WorkPlanDTO toWorkPlanDTO(WorkPlan workPlan) {
		return WorkPlanDTO.builder()
			.id(workPlan.getId())
			.process(workPlan.getProcess())
			.endTime(workPlan.getEndTime() != null ? workPlan.getEndTime().toString() : null)
			.startTime(workPlan.getStartTime() != null ? workPlan.getStartTime().toString() : null)
			.rawsCodes(workPlan.getRawsCodes())
			.facility(workPlan.getFacility())
			.lotCodes(workPlan.getLotCode() != null ? workPlan.getLotCode().getLotCode() : null)
			.activate(workPlan.isActivate())
			.lotCodes(workPlan.getLotCode() != null ? workPlan.getLotCode().getLotCode() : null)
			.build();
	}


}
