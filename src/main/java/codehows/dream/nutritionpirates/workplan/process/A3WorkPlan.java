package codehows.dream.nutritionpirates.workplan.process;

import static codehows.dream.nutritionpirates.workplan.process.CommonMethod.*;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public class A3WorkPlan implements WorkPlans {

	@Override
	public void execute(WorkPlan workPlan) {

	}

	@Override
	public WorkPlan createWorkPlan(int input) {
		expectTime(input);
		return WorkPlan.builder()
			.facility(Facility.extractor1)
			.process(Process.A3)
			.processCompletionTime(expectTime(input))
			.semiProduct("" + process(input))
			.build();
	}

	public String expectTime(int input) {
		double time = WORK_PLAN_DURATION.extractionDuration(input)
			+ WORK_PLAN_DURATION.extractionDuration(input);
		return getString(time);
	}

	//세척 후 75%로 수율
	public int process(int input) {
		return (int)Math.ceil(input * 0.2);
	}
}
