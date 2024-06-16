package codehows.dream.nutritionpirates.workplan.process;

import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.workplan.WorkPlanDuration;

public interface WorkPlans {
    final WorkPlanDuration WORK_PLAN_DURATION = new WorkPlanDuration();
    void execute();
    WorkPlan createWorkPlan(int input);
}
