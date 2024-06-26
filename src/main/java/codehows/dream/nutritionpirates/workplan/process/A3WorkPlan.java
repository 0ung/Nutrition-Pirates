package codehows.dream.nutritionpirates.workplan.process;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.FacilityStatus;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.constants.Routing;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.repository.WorkPlanRepository;
import codehows.dream.nutritionpirates.service.ProgramTimeService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class A3WorkPlan implements WorkPlans {

	private final ProgramTimeService programTimeService;
	private final WorkPlanRepository workPlanRepository;

	@Override
	public WorkPlan execute(WorkPlan workPlan) {
		Timestamp time = programTimeService.getProgramTime().getCurrentProgramTime();
		Timestamp comTime = getComplete(time, workPlan.getSemiProduct());
		WorkPlan plan = CommonMethod.setTime(workPlan, time, comTime);
		if (plan.getEndTime() != null) {
			plan.setFacilityStatus(FacilityStatus.AFTER_TREATMENT);
		}
		workPlanRepository.save(plan);
		return workPlan;
	}

	@Override
	public WorkPlan createWorkPlan(int input) {
		expectTime(input);
		return WorkPlan.builder()
			.facility(Facility.extractor1)
			.process(Process.A3)
			.processCompletionTime(expectTime(input))
			.semiProduct(process(input))
			.capacity(calCapacity(input))
			.facilityStatus(FacilityStatus.STANDBY)
			.build();
	}

	public int process(int input) {
		return (int)Math.ceil(input * 0.2);
	}

	public Timestamp expectTime(int input) {
		double time = WORK_PLAN_DURATION.extractionDuration(input) + WORK_PLAN_DURATION.extractionWaiting(input);
		int minToAdd = (int)time * 60;
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expectedTime = now.plusMinutes(minToAdd);
		return Timestamp.valueOf(expectedTime);
	}

	public Timestamp getComplete(Timestamp timestamp, int input) {
		double time = WORK_PLAN_DURATION.extractionDuration(input) + Routing.EXTRACTION_WAITING_TIME;
		int minToAdd = (int)time * 60;
		LocalDateTime localDateTime = timestamp.toLocalDateTime();
		LocalDateTime completeTime = localDateTime.plusMinutes(minToAdd);
		return Timestamp.valueOf(completeTime);
	}

	public int calCapacity(int input) {
		return (int) Math.ceil((double) input / Routing.EXTRACTION_ROUTING * 100);
	}
}
