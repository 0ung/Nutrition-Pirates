package codehows.dream.nutritionpirates.workplan.process;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.workplan.WorkPlanDuration;

public class A1WorkPlan implements WorkPlans {
    @Override
    public void execute() {

    }

    @Override
    public WorkPlan createWorkPlan(int input) {
        expectTime(input);
        return WorkPlan.builder()
                .facility(Facility.washer)
                .build();
    }

    public double expectTime(int input) {
        return WORK_PLAN_DURATION.washingDuration(input);
    }
}
