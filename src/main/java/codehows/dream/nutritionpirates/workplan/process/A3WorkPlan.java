package codehows.dream.nutritionpirates.workplan.process;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public class A3WorkPlan implements WorkPlans {
    @Override
    public void execute() {

    }

    @Override
    public WorkPlan createWorkPlan(int input) {
        expectTime(input);
        return WorkPlan.builder()
                .facility(Facility.extractor1)
                .build();
    }

    public double expectTime(int input) {
        double executeTime = WORK_PLAN_DURATION.extractionDuration(input);
        double waitingTime = WORK_PLAN_DURATION.extractionDuration(input);
        return executeTime + waitingTime;
    }
}
