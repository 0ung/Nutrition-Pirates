package codehows.dream.nutritionpirates.workplan.process;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.FacilityStatus;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.service.ProgramTimeService;
import codehows.dream.nutritionpirates.service.WorkPlanService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class A2WorkPlan implements WorkPlans {

	private final ProgramTimeService programTimeService;
	private final WorkPlanService workPlanService;

	@Override
	public WorkPlan execute(WorkPlan workPlan) {
		Timestamp time = programTimeService.getProgramTime().getCurrentProgramTime();

		workPlan.setStartTime(time);
		workPlan.setEndTime(time);
		workPlan.setActivate(false);
		return workPlan;
	}

	@Override
	public WorkPlan createWorkPlan(int input) {
		return WorkPlan.builder()
			.facility(Facility.weighing)
			.process(Process.A2)
			.semiProduct(input)
			.facilityStatus(FacilityStatus.STANDBY)
			.build();
	}
}
