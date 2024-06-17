package codehows.dream.nutritionpirates.workplan.process;

import static codehows.dream.nutritionpirates.workplan.process.CommonMethod.*;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public class B3WorkPlan implements WorkPlans {
	@Override
	public void execute() {

	}

	@Override
	public WorkPlan createWorkPlan(int input) {
		return WorkPlan.builder()
			.facility(Facility.metalDetector)
			.process(Process.B3)
			.processCompletionTime(expectTime(input))
			.semiProduct(""+input)
			.build();
	}

	public String expectTime(int input) {
		return getString(WORK_PLAN_DURATION.inspectionDuration(input) + WORK_PLAN_DURATION.inspectionWaiting(input));
	}
}
