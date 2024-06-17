package codehows.dream.nutritionpirates.workplan.process;

import static codehows.dream.nutritionpirates.workplan.process.CommonMethod.*;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public class B7WorkPlan implements WorkPlans {
	@Override
	public void execute() {

	}

	@Override
	public WorkPlan createWorkPlan(int input) {
		int data = process(input);
		return WorkPlan.builder()
			.facility(Facility.boxMachine)
			.process(Process.B7)
			.processCompletionTime(expectTime(data))
			.semiProduct("BOX" + data)
			.build();
	}

	public String expectTime(int input) {
		return getString(WORK_PLAN_DURATION.boxPackingDuration(input)
			+ WORK_PLAN_DURATION.boxPackingWaiting(input));
	}

	public int process(int input) {
		return (int)Math.floor(input / 25.0);
	}
}
