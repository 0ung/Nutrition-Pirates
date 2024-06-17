package codehows.dream.nutritionpirates.workplan.process;

import static codehows.dream.nutritionpirates.workplan.process.CommonMethod.*;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public class A6WorkPlan implements WorkPlans {
	@Override
	public void execute() {

	}

	@Override
	public WorkPlan createWorkPlan(int input) {
		int data = process(input);
		return WorkPlan.builder()
			.facility(Facility.juiceMachine1)
			.process(Process.A6)
			.processCompletionTime(expectTime(data))
			.semiProduct("" + data)
			.build();
	}

	public String expectTime(int input) {
		double executeTime = WORK_PLAN_DURATION.juicePackingDuration(input);
		double waitingTime = WORK_PLAN_DURATION.juicePackingWaiting(input);
		return getString(executeTime + waitingTime);
	}

	public int process(int input) {
		return (int)Math.floor(input * 1000 / 10.0);
	}
}
