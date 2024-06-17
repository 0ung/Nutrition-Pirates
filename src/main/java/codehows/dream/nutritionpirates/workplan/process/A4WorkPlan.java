package codehows.dream.nutritionpirates.workplan.process;

import static codehows.dream.nutritionpirates.workplan.process.CommonMethod.*;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public class A4WorkPlan implements WorkPlans {
	@Override
	public void execute() {

	}

	@Override
	public WorkPlan createWorkPlan(int input) {
		return WorkPlan.builder()
			.facility(Facility.filter)
			.process(Process.A4)
			.processCompletionTime(expectTime(input))
			.semiProduct(""+process(input))
			.build();
	}

	public String expectTime(int input) {
		double executeTime = WORK_PLAN_DURATION.filterDuration(input);
		double waitingTime = WORK_PLAN_DURATION.filterWaiting(input);
		return getString(executeTime + waitingTime);
	}

	public int process(int input){
		return (int) Math.ceil(input * 0.5);
	}
}
