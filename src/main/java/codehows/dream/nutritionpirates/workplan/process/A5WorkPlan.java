package codehows.dream.nutritionpirates.workplan.process;

import static codehows.dream.nutritionpirates.workplan.process.CommonMethod.*;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public class A5WorkPlan implements WorkPlans {
    @Override
    public void execute(WorkPlan workPlan) {

    }

    @Override
    public WorkPlan createWorkPlan(int input) {
        return WorkPlan.builder()
                .facility(Facility.sterilizer1)
            .process(Process.A5)
            .processCompletionTime(expectTime(input))
            .semiProduct("즙 "+ input)
            .build();
    }

    public String expectTime(int input) {
        double executeTime = WORK_PLAN_DURATION.sterilizationDuration(input);
        double waitingTime = WORK_PLAN_DURATION.sterilizationWaiting(input);
        return getString(executeTime + waitingTime);
    }
}
