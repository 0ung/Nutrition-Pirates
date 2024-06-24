package codehows.dream.nutritionpirates.workplan.process;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;

import org.springframework.stereotype.Component;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.repository.WorkPlanRepository;
import codehows.dream.nutritionpirates.service.ProgramTimeService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class A5WorkPlan implements WorkPlans {

	private final ProgramTimeService programTimeService;
	private final WorkPlanRepository workPlanRepository;

	@Override
	public WorkPlan execute(WorkPlan workPlan) {
		Timestamp time = programTimeService.getProgramTime().getCurrentProgramTime();
		Timestamp comTime = getComplete(time, workPlan.getSemiProduct());
		WorkPlan plan = CommonMethod.setTime(workPlan, time, comTime);
		workPlanRepository.save(plan);
		return workPlan;

	}

	@Override
	public WorkPlan createWorkPlan(int input) {
		return WorkPlan.builder()
			.facility(Facility.sterilizer1)
			.process(Process.A5)
			.processCompletionTime(expectTime(input))
			.semiProduct(input)
			.build();
	}

	public Timestamp expectTime(int input) {
		double time = WORK_PLAN_DURATION.sterilizationDuration(input);
		int minToAdd = (int) time * 60;
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expectedTime = now.plusMinutes(minToAdd);
		return Timestamp.valueOf(expectedTime);
	}

	public Timestamp getComplete(Timestamp timestamp, int input) {
		double time = WORK_PLAN_DURATION.sterilizationDuration(input);
		int minToAdd = (int) time * 60;
		LocalDateTime localDateTime = timestamp.toLocalDateTime();
		LocalDateTime completeTime = localDateTime.plusMinutes(minToAdd);
		return Timestamp.valueOf(completeTime);
	}

}
