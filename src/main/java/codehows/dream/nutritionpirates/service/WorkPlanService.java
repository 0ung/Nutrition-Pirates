package codehows.dream.nutritionpirates.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.dto.ActivateFacilityDTO;
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

	public void createJuiceProcessPlan(ProcessPlan processPlan) {

		juiceProcess.forEach(
			(e) -> {
				WorkPlans workPlans = WorkPlanFactoryProvider.createWorkOrder(e);
				WorkPlan workPlan = null;

				if (e.equals(Process.A1)) {
					workPlan = workPlans.createWorkPlan(5000);
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
		Order order = orderRepository.findById(processPlan.getOrder().getId()).orElse(null);

		if (order == null) {
			throw new NotFoundOrderException("수주 정보가 없습니다.");
		}
		order.updateExpectedDeliveryDate(expectDeliveryDate(processPlan));
	}

	public void createStickProcessPlan(ProcessPlan processPlan) {
		stickProcess.forEach(
			(e) -> {
				WorkPlans workPlans = WorkPlanFactoryProvider.createWorkOrder(e);
				WorkPlan workPlan = null;

				if (e.equals(Process.B1)) {
					workPlan = workPlans.createWorkPlan(60);
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

	private List<WorkPlan> activateFacility(Facility facility) {
		List<WorkPlan> workPlans = workPlanRepository.findByFacility(facility);
		if (workPlans.isEmpty()) {
			return null;
		}
		return workPlans;
	}

	public ActivateFacilityDTO getActivateFacility(Facility[] facilitys) {

		ActivateFacilityDTO facilityDTO = new ActivateFacilityDTO();

		for (Facility facility : facilitys) {
			List<WorkPlan> workPlan = activateFacility(facility);

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
		WorkPlan executedWork = work.execute(workPlan);

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
}
