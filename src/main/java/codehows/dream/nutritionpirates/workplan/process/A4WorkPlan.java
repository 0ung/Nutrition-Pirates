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
public class A4WorkPlan implements WorkPlans {

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
		return WorkPlan.builder()
			.facility(Facility.filter)
			.process(Process.A4)
			.processCompletionTime(expectTime(input))
			.semiProduct(process(input))
			.build();
	}

	public int process(int input) {
		return (int)Math.ceil(input * 0.5);
	}

	public Timestamp expectTime(int input) {
		double time = WORK_PLAN_DURATION.filterDuration(input);
		int minToAdd = (int)time * 60;

		// 현재 시간을 LocalDateTime으로 가져오기
		LocalDateTime now = LocalDateTime.now();

		// 분 추가
		LocalDateTime expectedTime = now.plusMinutes(minToAdd);

		// LocalDateTime을 Timestamp으로 변환하여 반환
		return Timestamp.valueOf(expectedTime);
	}

	public Timestamp getComplete(Timestamp timestamp, int input) {
		double time = WORK_PLAN_DURATION.filterDuration(input);
		int minToAdd = (int)time * 60;

		// 입력된 Timestamp를 LocalDateTime으로 변환
		LocalDateTime localDateTime = timestamp.toLocalDateTime();

		// 분 추가
		LocalDateTime completeTime = localDateTime.plusMinutes(minToAdd);

		// LocalDateTime을 Timestamp으로 변환하여 반환
		return Timestamp.valueOf(completeTime);
	}

	public LotCode getLotCode(WorkPlan workPlan, Timestamp time) {
		String lotCode = CommonMethod.getLotCode(workPlan, time);
		WorkPlan preWorkPlan = workPlanRepository.findByProcessPlanIdAndFacility(workPlan.getProcessPlan().getId(),
			Facility.washer);
		if (preWorkPlan == null) {
			throw new NotFoundWorkPlanException();
		}
		String preLotCode = preWorkPlan.getLotCode().getLetCode();

		return new LotCode(lotCode, preLotCode);
	}
}
