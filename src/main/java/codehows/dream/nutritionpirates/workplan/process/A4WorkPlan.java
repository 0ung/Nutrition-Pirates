package codehows.dream.nutritionpirates.workplan.process;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public class A4WorkPlan implements WorkPlans {
    @Override
    public void execute() {

    }

    @Override
    public WorkPlan createWorkPlan() {
        return WorkPlan.builder()
                .facility(Facility.filter)
                .build();
    }

    @Override
    public double expectTime(int input) {
        double executeTime = WORK_PLAN_DURATION.filterDuration(input);
        double waitingTime = WORK_PLAN_DURATION.filterWaiting(input);
        return executeTime + waitingTime;
    }
}
