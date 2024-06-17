package codehows.dream.nutritionpirates.workplan.process;

import static codehows.dream.nutritionpirates.workplan.process.CommonMethod.*;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public class B4WorkPlan implements WorkPlans {
	@Override
	public void execute() {

	}

	@Override
	public WorkPlan createWorkPlan(int input) {
		int data = process(input);
		return WorkPlan.builder()
			.facility(Facility.juiceMachine1)
			.process(Process.B4)
			.processCompletionTime(expectTime(data))
			.semiProduct("" + data)
			.build();
	}

	public String expectTime(int input) {
		return getString(WORK_PLAN_DURATION.stickPackingDuration(input) +
			WORK_PLAN_DURATION.stickPackingWaiting(input));
	}

	public int process(int input) {
		return (int)Math.floor(input * 1000.0 / 5.0);
	}
}
