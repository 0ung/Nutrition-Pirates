package codehows.dream.nutritionpirates.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.FacilityStatus;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.dto.ActivateFacilityDTO;
import codehows.dream.nutritionpirates.dto.RawBOMDTO;
import codehows.dream.nutritionpirates.dto.WorkPlanDTO;
import codehows.dream.nutritionpirates.entity.Order;
import codehows.dream.nutritionpirates.entity.ProcessPlan;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.exception.NotFoundOrderException;
import codehows.dream.nutritionpirates.exception.NotFoundWorkPlanException;
import codehows.dream.nutritionpirates.repository.OrderRepository;
import codehows.dream.nutritionpirates.repository.WorkPlanRepository;
import codehows.dream.nutritionpirates.workplan.WorkPlanFactoryProvider;
import codehows.dream.nutritionpirates.workplan.process.A1WorkPlan;
import codehows.dream.nutritionpirates.workplan.process.A2WorkPlan;
import codehows.dream.nutritionpirates.workplan.process.A3WorkPlan;
import codehows.dream.nutritionpirates.workplan.process.A4WorkPlan;
import codehows.dream.nutritionpirates.workplan.process.A5WorkPlan;
import codehows.dream.nutritionpirates.workplan.process.A6WorkPlan;
import codehows.dream.nutritionpirates.workplan.process.A7WorkPlan;
import codehows.dream.nutritionpirates.workplan.process.A8WorkPlan;
import codehows.dream.nutritionpirates.workplan.process.B1WorkPlan;
import codehows.dream.nutritionpirates.workplan.process.B2WorkPlan;
import codehows.dream.nutritionpirates.workplan.process.B3WorkPlan;
import codehows.dream.nutritionpirates.workplan.process.B4WorkPlan;
import codehows.dream.nutritionpirates.workplan.process.B5WorkPlan;
import codehows.dream.nutritionpirates.workplan.process.B6WorkPlan;
import codehows.dream.nutritionpirates.workplan.process.B7WorkPlan;
import codehows.dream.nutritionpirates.workplan.process.WorkPlans;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class WorkPlanService {

	private final WorkPlanRepository workPlanRepository;
	private final OrderRepository orderRepository;
	private final ApplicationContext context;
	private final RawOrderInsertService rawOrderInsertService;
	private final BOMCalculatorService bomCalculatorService;
	private final ProgramTimeService programTimeService;
	private int semiProduct = 0;
	//즙 공정
	private static final List<Process> juiceProcess = new ArrayList<>(
		List.of(Process.A1,
			Process.A2,
			Process.A3,
			Process.A4,
			Process.A5,
			Process.A6,
			Process.A7,
			Process.A8
		)
	);
	//스틱 공정
	private static final List<Process> stickProcess = new ArrayList<>(
		List.of(
			Process.B1,
			Process.B2,
			Process.B3,
			Process.B4,
			Process.B5,
			Process.B6,
			Process.B7
		)
	);

	public RawBOMDTO calInputRaws(Order order) {
		return bomCalculatorService.createRequirement(order);
	}

	public void createJuiceProcessPlan(ProcessPlan processPlan) {

		Order order = orderRepository.findById(processPlan.getOrder().getId()).orElse(null);
		if (order == null) {
			throw new NotFoundOrderException();
		}
		RawBOMDTO raws = calInputRaws(order);
		juiceProcess.forEach(
			(e) -> {
				WorkPlans workPlans = WorkPlanFactoryProvider.createWorkOrder(e);
				WorkPlan workPlan = null;

				if (e.equals(Process.A1)) {
					workPlan = workPlans.createWorkPlan((int)Math.ceil(raws.getIngredient1()));
					semiProduct = workPlan.getSemiProduct();
				} else if (e.equals(Process.A3)) {
					workPlan = workPlans.createWorkPlan(semiProduct);
					semiProduct = workPlan.getSemiProduct();
				} else if (e.equals(Process.A4)) {
					workPlan = workPlans.createWorkPlan(semiProduct);
					semiProduct = workPlan.getSemiProduct();
				} else if (e.equals(Process.A6)) {
					workPlan = workPlans.createWorkPlan(semiProduct);
					semiProduct = workPlan.getSemiProduct();
				} else {
					workPlan = workPlans.createWorkPlan(semiProduct);
				}
				workPlan.setProcessPlan(processPlan);
				workPlanRepository.save(workPlan);
			}
		);

		order.updateExpectedDeliveryDate(expectDeliveryDate(processPlan));
	}

	public void createStickProcessPlan(ProcessPlan processPlan) {
		Order order = orderRepository.findById(processPlan.getOrder().getId()).orElse(null);
		if (order == null) {
			throw new NotFoundOrderException();
		}
		RawBOMDTO raws = calInputRaws(order);

		stickProcess.forEach(
			(e) -> {
				WorkPlans workPlans = WorkPlanFactoryProvider.createWorkOrder(e);
				WorkPlan workPlan = null;

				if (e.equals(Process.B1)) {
					workPlan = workPlans.createWorkPlan((int)Math.ceil(raws.getIngredient1()));
					semiProduct = workPlan.getSemiProduct();
				} else if (e.equals(Process.B4)) {
					workPlan = workPlans.createWorkPlan(semiProduct);
					semiProduct = workPlan.getSemiProduct();
				} else {
					workPlan = workPlans.createWorkPlan(semiProduct);
				}

				workPlan.setProcessPlan(processPlan);
				workPlanRepository.save(workPlan);
			}
		);
	}

	public String expectDeliveryDate(ProcessPlan processPlan) {
		List<WorkPlan> list = workPlanRepository.findAllByProcessPlanId(processPlan.getId());
		long deliveryTimeMinutes = list.stream().mapToLong(workPlan -> {
			if (workPlan.getProcessCompletionTime() == null) {
				return 0;
			}
			Timestamp timestamp = workPlan.getProcessCompletionTime();
			Timestamp nowTimestamp = Timestamp.valueOf(LocalDateTime.now());
			return ChronoUnit.MINUTES.between(nowTimestamp.toLocalDateTime(), timestamp.toLocalDateTime());
		}).sum();

		Timestamp currentProgramTime = programTimeService.getProgramTime().getCurrentProgramTime();
		return currentProgramTime.toLocalDateTime().plusMinutes(deliveryTimeMinutes).toString();
	}

	private List<WorkPlan> getActivateFacility(Facility facility) {
		List<WorkPlan> workPlans = workPlanRepository.findByFacility(facility);
		if (workPlans.isEmpty()) {
			return null;
		}
		return workPlans;
	}

	public ActivateFacilityDTO getActivateFacility(Facility[] facilitys) {

		ActivateFacilityDTO facilityDTO = new ActivateFacilityDTO();

		for (Facility facility : facilitys) {
			List<WorkPlan> workPlan = getActivateFacility(facility);

			if (workPlan == null || workPlan.isEmpty()) {
				log.error("작업지시가 없음");
				continue;
			}

			List<WorkPlanDTO> workPlans = workPlan.stream().map(WorkPlanDTO::toWorkPlanDTO).toList();
			switch (facility) {
				case juiceMachine1 -> facilityDTO.setJuiceMachine1(workPlans);
				case juiceMachine2 -> facilityDTO.setJuiceMachine2(workPlans);
				case boxMachine -> facilityDTO.setBoxMachine(workPlans);
				case metalDetector -> facilityDTO.setMetalDetector(workPlans);
				case sterilizer1 -> facilityDTO.setSterilizer1(workPlans);
				case extractor2 -> facilityDTO.setExtractor2(workPlans);
				case sterilizer2 -> facilityDTO.setSterilizer2(workPlans);
				case StickMachine1 -> facilityDTO.setStickMachine1(workPlans);
				case StickMachine2 -> facilityDTO.setStickMachine2(workPlans);
				case washer -> facilityDTO.setWasher(workPlans);
				case mixer -> facilityDTO.setMixer(workPlans);
				case extractor1 -> facilityDTO.setExtractor1(workPlans);
			}
		}

		return facilityDTO;
	}

	public WorkPlanDTO executeWork(Long id) {
		WorkPlan workPlan = workPlanRepository.findById(id).orElse(null);
		if (workPlan == null)
			throw new NotFoundWorkPlanException();
		WorkPlans work = getWorkPlanByProcess(workPlan.getProcess());
		if (work == null)
			throw new NotFoundWorkPlanException();
		WorkPlan executedWork;
		executedWork = work.execute(workPlan);
		Process next = null;

		if (executedWork.getProcess().toString().contains("A")) {
			next = checkNextAProcess(executedWork.getProcess()).orElse(null);
		} else {
			next = checkNextBProcess(executedWork.getProcess()).orElse(null);
		}
		List<WorkPlan> list = getFacilities(next);
		if (list == null) {

		}
		// 설비 상태 조회

		return WorkPlanDTO.builder()
			.id(executedWork.getId())
			.endTime(executedWork.getEndTime() != null ? executedWork.getEndTime().toString() : null)
			.startTime(executedWork.getStartTime().toString())
			.lotCodes(executedWork.getLotCode() != null ? executedWork.getLotCode().getLetCode() : null)
			.facility(executedWork.getFacility())
			.rawsCodes(executedWork.getRawsCodes())
			.process(executedWork.getProcess())
			.build();
	}

	private WorkPlans getWorkPlanByProcess(Process process) {

		WorkPlans workPlans = null;
		switch (process) {
			case A1 -> workPlans = context.getBean(A1WorkPlan.class);
			case A2 -> workPlans = context.getBean(A2WorkPlan.class);
			case A3 -> workPlans = context.getBean(A3WorkPlan.class);
			case A4 -> workPlans = context.getBean(A4WorkPlan.class);
			case A5 -> workPlans = context.getBean(A5WorkPlan.class);
			case A6 -> workPlans = context.getBean(A6WorkPlan.class);
			case A7 -> workPlans = context.getBean(A7WorkPlan.class);
			case A8 -> workPlans = context.getBean(A8WorkPlan.class);
			case B1 -> workPlans = context.getBean(B1WorkPlan.class);
			case B2 -> workPlans = context.getBean(B2WorkPlan.class);
			case B3 -> workPlans = context.getBean(B3WorkPlan.class);
			case B4 -> workPlans = context.getBean(B4WorkPlan.class);
			case B5 -> workPlans = context.getBean(B5WorkPlan.class);
			case B6 -> workPlans = context.getBean(B6WorkPlan.class);
			case B7 -> workPlans = context.getBean(B7WorkPlan.class);
			default -> {
				return null;
			}
		}
		return workPlans;
	}

	//@TODO 대기시간 계산 완료
	//대기시간은 그냥 실제 가동시간에 추가해서 진행
	//@TODO 작업지시 활성화
	//조건 1. 다음공정이 사용될 장비가 사용 중인지?
	//조건 2. 다음공정 장비가 후처리 중인지?

	//다음공정 계산
	// A 공정 그룹에서 다음 공정을 확인하는 메서드
	private Optional<Process> checkNextAProcess(Process currentProcess) {
		Process[] aProcesses = {Process.A1, Process.A2, Process.A3, Process.A4, Process.A5, Process.A6, Process.A7,
			Process.A8};
		for (int i = 0; i < aProcesses.length - 1; i++) {
			if (aProcesses[i] == currentProcess) {
				return Optional.of(aProcesses[i + 1]);
			}
		}
		return Optional.empty(); // 마지막 공정이거나 해당하는 공정이 없을 경우
	}

	// B 공정 그룹에서 다음 공정을 확인하는 메서드
	private Optional<Process> checkNextBProcess(Process currentProcess) {
		Process[] bProcesses = {Process.B1, Process.B2, Process.B3, Process.B4, Process.B5, Process.B6, Process.B7};
		for (int i = 0; i < bProcesses.length - 1; i++) {
			if (bProcesses[i] == currentProcess) {
				return Optional.of(bProcesses[i + 1]);
			}
		}
		return Optional.empty(); // 마지막 공정이거나 해당하는 공정이 없을 경우
	}

	//각 공정별 기계 조회
	private List<WorkPlan> getFacilities(Process process) {
		if (process == Process.A1) {
			return getFacilityStatus(Facility.washer);
		}
		if (process == Process.A4) {
			return getFacilityStatus(Facility.filter);
		}
		if (process == Process.A7 || process == Process.B6) {
			return getFacilityStatus(Facility.metalDetector);
		}
		if (process == Process.B2) {
			return getFacilityStatus(Facility.mixer);
		}
		if (process == Process.B5) {
			return getFacilityStatus(Facility.freeze);
		}
		if (process == Process.A3) {
			getFacilityAfterTreatmentStatus(Facility.extractor1, Facility.extractor2);
			return getFacilityStatus(Facility.extractor1, Facility.extractor2);
		}
		if (process == Process.A5 || process == Process.B3) {
			return getFacilityStatus(Facility.sterilizer1, Facility.sterilizer2);
		}
		if (process == Process.A6) {
			return getFacilityStatus(Facility.juiceMachine1, Facility.juiceMachine2);
		}
		if (process == Process.B4) {
			return getFacilityStatus(Facility.StickMachine1, Facility.StickMachine2);
		}
		return null;
	}

	private WorkPlan getFacilityWorking(Facility facility) {
		return workPlanRepository.findByFacilityStatusAndFacility(FacilityStatus.WORKING, facility);
	}

	private WorkPlan getFacilityAfterTreatment(Facility facility) {
		return workPlanRepository.findByFacilityStatusAndFacility(FacilityStatus.AFTER_TREATMENT, facility);
	}

	private List<WorkPlan> getFacilityStatus(Facility facility1, Facility facility2) {
		ArrayList<WorkPlan> list = new ArrayList<>();
		WorkPlan plan = getFacilityWorking(facility1);
		WorkPlan plans = getFacilityWorking(facility2);
		if (plan != null && plans != null) {
			list.add(0, plan);
			list.add(1, plans);
		}
		if (plan == null) {
			list.add(0, plans);
		}
		return list;
	}

	private void getFacilityAfterTreatmentStatus(Facility facility1, Facility facility2) {
		ArrayList<WorkPlan> list = new ArrayList<>();
		WorkPlan plan = getFacilityAfterTreatment(facility1);
		WorkPlan plans = getFacilityAfterTreatment(facility2);
		if (calTime(plan)) {
			plan.setFacilityStatus(FacilityStatus.STANDBY);
			workPlanRepository.save(plan);
		} else if (calTime(plans)) {
			plans.setFacilityStatus(FacilityStatus.STANDBY);
			workPlanRepository.save(plans);
		}
	}

	private List<WorkPlan> getFacilityStatus(Facility facility) {
		List<WorkPlan> list = new ArrayList<>();
		WorkPlan plan = getFacilityWorking(facility);
		if (plan != null) {
			list.add(0, plan);
		}
		return list;
	}

	private boolean calTime(WorkPlan workPlan) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(workPlan.getEndTime());
		calendar.add(Calendar.DAY_OF_YEAR, 1);

		Timestamp oneDayAfterEndTime = new Timestamp(calendar.getTime().getTime());

		return workPlan.getEndTime().after(oneDayAfterEndTime);
	}
}
