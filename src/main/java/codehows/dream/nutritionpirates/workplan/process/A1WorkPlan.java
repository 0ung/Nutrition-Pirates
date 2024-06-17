package codehows.dream.nutritionpirates.workplan.process;

import static codehows.dream.nutritionpirates.workplan.process.CommonMethod.*;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public class A1WorkPlan implements WorkPlans {
	@Override
	public void execute(WorkPlan workPlan) {
	}

	@Override
	public WorkPlan createWorkPlan(int input) {

		return WorkPlan.builder()
			.facility(Facility.washer)
			.process(Process.A1)
			.processCompletionTime(expectTime(input))
			.semiProduct(""+process(input))
			.build();
	}

	public String expectTime(int input) {
		double time = WORK_PLAN_DURATION.washingDuration(input);
		return getString(time);
	}

	public int process(int input) {
		return (int)Math.ceil(input * 0.75);
	}

}
