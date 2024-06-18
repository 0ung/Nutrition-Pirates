package codehows.dream.nutritionpirates.workplan.process;

import static codehows.dream.nutritionpirates.workplan.process.CommonMethod.*;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public class B6WorkPlan implements WorkPlans {
	@Override
	public void execute(WorkPlan workPlan) {

	}

	@Override
	public WorkPlan createWorkPlan(int input) {
		return WorkPlan.builder()
			.facility(Facility.metalDetector)
			.process(Process.B6)
			.build();
	}

	public String expectTime(int input) {
		return getString(WORK_PLAN_DURATION.inspectionDuration(input) + WORK_PLAN_DURATION.inspectionWaiting(input));
	}
}
