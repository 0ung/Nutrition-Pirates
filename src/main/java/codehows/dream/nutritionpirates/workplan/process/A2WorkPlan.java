package codehows.dream.nutritionpirates.workplan.process;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public class A2WorkPlan implements WorkPlans {
    @Override
    public void execute() {

    }

    @Override
    public WorkPlan createWorkPlan(int input) {
        return WorkPlan.builder()
                .facility(Facility.weighing)
                .build();
    }
}
