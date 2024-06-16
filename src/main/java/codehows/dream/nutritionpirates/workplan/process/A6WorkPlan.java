package codehows.dream.nutritionpirates.workplan.process;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public class A6WorkPlan implements WorkPlans {
    @Override
    public void execute() {

    }

    @Override
    public WorkPlan createWorkPlan() {
        return WorkPlan.builder()
                .facility(Facility.juiceMachine1)
                .build();
    }

    @Override
    public double expectTime(int input) {
        double executeTime = WORK_PLAN_DURATION.juicePackingDuration(input);
        double waitingTime = WORK_PLAN_DURATION.juicePackingWaiting(input);

        return executeTime + waitingTime;
    }
}
