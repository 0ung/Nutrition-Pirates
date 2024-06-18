package codehows.dream.nutritionpirates.workplan.process;

import static codehows.dream.nutritionpirates.workplan.process.CommonMethod.*;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public class B1WorkPlan implements WorkPlans {
	@Override
	public void execute(WorkPlan workPlan) {

	}

	@Override
	public WorkPlan createWorkPlan(int input) {

		return WorkPlan.builder()
			.facility(Facility.mixer)
			.process(Process.B1)
			.processCompletionTime(expectTime(input))
			.semiProduct("" + input)
			.build();
	}

	private String expectTime(int input) {
		return getString(WORK_PLAN_DURATION.mixDuration(input) + WORK_PLAN_DURATION.mixWaiting(input));
	}
}
