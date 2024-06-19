package codehows.dream.nutritionpirates.workplan.process;

import static codehows.dream.nutritionpirates.workplan.process.CommonMethod.*;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.repository.WorkPlanRepository;
import codehows.dream.nutritionpirates.service.ProgramTimeService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class B1WorkPlan implements WorkPlans {

	private final ProgramTimeService programTimeService;
	private final WorkPlanRepository workPlanRepository;

	@Override
	public WorkPlan execute(WorkPlan workPlan) {
		Timestamp time = programTimeService.getProgramTime().getCurrentProgramTime();

		workPlan.setStartTime(time);
		workPlan.setEndTime(time);
		workPlan.setRawsCodes("일단 더미");
		workPlan.setActivate(false);

		//@TODO 이거 변수 처리해야됨
		workPlan.setWorker("김영웅");
		return workPlan;
	}

	@Override
	public WorkPlan createWorkPlan(int input) {

		return WorkPlan.builder()
			.facility(Facility.weighing)
			.process(Process.B1)
			.semiProduct(input)
			.build();
	}
}
