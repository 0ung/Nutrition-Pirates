package codehows.dream.nutritionpirates.workplan.process;

import static codehows.dream.nutritionpirates.workplan.process.CommonMethod.*;

import java.sql.Time;
import java.sql.Timestamp;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.service.ProgramTimeService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class A1WorkPlan implements WorkPlans {

	private final ProgramTimeService programTimeService;
	@Override
	public void execute(WorkPlan workPlan) {
		Timestamp timestamp = programTimeService.getProgramTime().getCurrentProgramTime();
		if(workPlan.getStartTime() == null){
			workPlan.setEndTime(timestamp);
			return;
		}
		workPlan.setStartTime(timestamp);
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
