package codehows.dream.nutritionpirates.workplan.process;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public class A5WorkPlan implements WorkPlans {
    @Override
    public void execute() {

    }

    @Override
    public WorkPlan createWorkPlan() {
        return WorkPlan.builder()
                .facility(Facility.sterilizer1).build();
    }

    @Override
    public double expectTime(int input) {
        double executeTime = WORK_PLAN_DURATION.sterilizationDuration(input);
        double waitingTime = WORK_PLAN_DURATION.sterilizationWaiting(input);
        return executeTime + waitingTime;
    }
}
