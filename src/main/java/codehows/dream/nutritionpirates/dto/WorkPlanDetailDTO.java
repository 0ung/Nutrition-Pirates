package codehows.dream.nutritionpirates.dto;

import java.sql.Timestamp;

import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;
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
public class WorkPlanDetailDTO {
	private Long processPlanId;
	private Process process;
	private String rawsId;
	private String lotCode;
	private String processStatus;
	private String processCompletionTime;
	private String worker;

    public static WorkPlanDetailDTO toWorkPlanDetailDTO(WorkPlan workPlan,Timestamp time) {
        return WorkPlanDetailDTO.builder()
                .processPlanId(workPlan.getProcessPlan().getId())
                .process(workPlan.getProcess())
                .rawsId(workPlan.getRawsCodes() == null ? "없음" : workPlan.getRawsCodes())
                .lotCode(workPlan.getLotCode() ==null? "없음" : workPlan.getLotCode().getLotCode())
                .processStatus(calProcess(workPlan,time))
                .processCompletionTime(workPlan.getProcessCompletionTime() ==null? "없음" : workPlan.getProcessCompletionTime().toLocalDateTime().toString())
                .worker(workPlan.getWorker())
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
		if (Double.isNaN(progress)) {
			return "0%";
		}
		if(Double.isInfinite(progress)){
			return "100%";
		}
		return String.format("%.2f%%", Math.ceil(progress));
	}
}
