package codehows.dream.nutritionpirates.workplan.process;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public class A2WorkPlan implements WorkPlans {

	@Override
	public void execute(WorkPlan workPlan) {

	}

	@Override
	public WorkPlan createWorkPlan(int input) {
		return WorkPlan.builder()
			.facility(Facility.weighing)
			.process(Process.A2)
			.semiProduct("양배추 / 흑마늘" + input)
			.build();
	}

	public String expectTime(int input) {
		return 0 + "분";
	}
}
