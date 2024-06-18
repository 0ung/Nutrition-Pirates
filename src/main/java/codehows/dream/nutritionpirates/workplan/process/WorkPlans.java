package codehows.dream.nutritionpirates.workplan.process;

import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.service.ProgramTimeService;
import codehows.dream.nutritionpirates.workplan.WorkPlanDuration;
import lombok.RequiredArgsConstructor;

public interface WorkPlans {
    WorkPlanDuration WORK_PLAN_DURATION = new WorkPlanDuration();

    void execute(WorkPlan workPlan);
    WorkPlan createWorkPlan(int input);
}
