package codehows.dream.nutritionpirates.dto;

import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import lombok.*;

import java.sql.Timestamp;

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
                .lotCode(workPlan.getLotCode() ==null? "없음" : workPlan.getLotCode().getLetCode())
                .processStatus(calProcess(workPlan,time))
                .processCompletionTime(workPlan.getProcessCompletionTime().toLocalDateTime().toString())
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

        return String.format("Progress: %.2f%%", progress);
    }
}
