package codehows.dream.nutritionpirates.dto;

import java.sql.Timestamp;

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
	private String processStatus;

	public static WorkPlanDTO toWorkPlanDTO(WorkPlan workPlan, Timestamp time) {
		return WorkPlanDTO.builder()
			.id(workPlan.getId())
			.process(workPlan.getProcess())
			.endTime(workPlan.getEndTime() != null ? workPlan.getEndTime().toString() : null)
			.startTime(workPlan.getStartTime() != null ? workPlan.getStartTime().toString() : null)
			.rawsCodes(workPlan.getRawsCodes())
			.facility(workPlan.getFacility())
			.activate(workPlan.isActivate())
			.lotCodes(workPlan.getLotCode() != null ? workPlan.getLotCode().getLotCode() : null)
			.processStatus(calProcess(workPlan, time))
			.build();

	}

	public static String calProcess(WorkPlan plan, Timestamp time) {
		if (plan.getStartTime() == null || plan.getProcessCompletionTime() == null) {
			return null;
		}
		long startTime = plan.getStartTime().getTime();
		long endTime = plan.getProcessCompletionTime().getTime();
		long currentTime = time.getTime();

		// Calculate the progress percentage
		long elapsedTime = currentTime - startTime;
		long totalProcessTime = endTime - startTime;
		double progress = ((double)elapsedTime / totalProcessTime) * 100;

		return String.format("Progress: %.2f%%", progress);
	}

}
