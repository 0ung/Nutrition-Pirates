package codehows.dream.nutritionpirates.workplan.process;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public class A8WorkPlan implements WorkPlans {
    @Override
    public void execute() {

    }

    @Override
    public WorkPlan createWorkPlan() {
        return WorkPlan.builder()
                .facility(Facility.boxMachine)
                .build();
    }

    @Override
    public double expectTime(int input) {
        return WORK_PLAN_DURATION.boxPackingDuration(input) + WORK_PLAN_DURATION.boxPackingWaiting(input);
    }
}
