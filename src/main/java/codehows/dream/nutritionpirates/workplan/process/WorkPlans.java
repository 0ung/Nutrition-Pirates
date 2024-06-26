package codehows.dream.nutritionpirates.workplan.process;

import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.workplan.WorkPlanDuration;

public interface WorkPlans {
	WorkPlanDuration WORK_PLAN_DURATION = new WorkPlanDuration();

	WorkPlan execute(WorkPlan workPlan);

	WorkPlan createWorkPlan(int input);

}
