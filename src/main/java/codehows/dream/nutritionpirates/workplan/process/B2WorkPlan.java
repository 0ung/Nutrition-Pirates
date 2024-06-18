package codehows.dream.nutritionpirates.workplan.process;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public class B2WorkPlan implements WorkPlans {
    @Override
    public void execute(WorkPlan workPlan) {

    }

    @Override
    public WorkPlan createWorkPlan(int input) {

        return WorkPlan.builder()
            .facility(Facility.weighing)
            .process(Process.B2)
            .processCompletionTime(expectTime(input))
            .semiProduct(""+input)
            .build();

    }

    public String expectTime(int input) {
        return "0";
    }
}
