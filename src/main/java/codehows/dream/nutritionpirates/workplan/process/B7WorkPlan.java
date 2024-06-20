package codehows.dream.nutritionpirates.workplan.process;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.LotCode;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.exception.NotFoundWorkPlanException;
import codehows.dream.nutritionpirates.repository.LotCodeRepository;
import codehows.dream.nutritionpirates.repository.WorkPlanRepository;
import codehows.dream.nutritionpirates.service.ProgramTimeService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class B7WorkPlan implements WorkPlans {

	private final ProgramTimeService programTimeService;
	private final WorkPlanRepository workPlanRepository;
	private final LotCodeRepository lotCodeRepository;

	@Override
	public WorkPlan execute(WorkPlan workPlan) {
		Timestamp time = programTimeService.getProgramTime().getCurrentProgramTime();
		Timestamp comTime = getComplete(time, workPlan.getSemiProduct());
		WorkPlan plan = CommonMethod.setTime(workPlan, time, comTime);
		LotCode lotCode = getLotCode(workPlan, time);
		lotCodeRepository.saveAndFlush(lotCode);
		plan.setLotCode(lotCode);

		workPlanRepository.save(plan);
		return workPlan;
	}

	@Override
	public WorkPlan createWorkPlan(int input) {
		int data = process(input);
		return WorkPlan.builder()
			.facility(Facility.boxMachine)
			.process(Process.B7)
			.processCompletionTime(expectTime(data))
			.semiProduct(data)
			.build();
	}

	public Timestamp expectTime(int input) {
		double time = WORK_PLAN_DURATION.boxPackingDuration(input);
		int minToAdd = (int)time * 60;
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expectedTime = now.plusMinutes(minToAdd);
		return Timestamp.valueOf(expectedTime);
	}

	public Timestamp getComplete(Timestamp timestamp, int input) {
		double time = WORK_PLAN_DURATION.boxPackingDuration(input);
		int minToAdd = (int)time * 60;
		LocalDateTime localDateTime = timestamp.toLocalDateTime();
		LocalDateTime completeTime = localDateTime.plusMinutes(minToAdd);
		return Timestamp.valueOf(completeTime);
	}

	public int process(int input) {
		return (int)Math.floor(input / 25.0);
	}

	public LotCode getLotCode(WorkPlan workPlan, Timestamp time) {
		String lotCode = CommonMethod.getLotCode(workPlan, time);
		WorkPlan preWorkPlan = workPlanRepository.findByProcessPlanIdAndFacility(workPlan.getProcessPlan().getId(),
			Facility.mixer);
		if (preWorkPlan == null) {
			throw new NotFoundWorkPlanException();
		}
		String preLotCode = preWorkPlan.getLotCode().getLetCode();

		return new LotCode(lotCode, preLotCode);
	}
}
