package codehows.dream.nutritionpirates.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.FacilityStatus;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.constants.ProductName;
import codehows.dream.nutritionpirates.constants.Routing;
import codehows.dream.nutritionpirates.dto.ActivateFacilityDTO;
import codehows.dream.nutritionpirates.dto.RawBOMDTO;
import codehows.dream.nutritionpirates.dto.WorkPlanDTO;
import codehows.dream.nutritionpirates.dto.WorkPlanDetailDTO;
import codehows.dream.nutritionpirates.dto.WorkPlanListDTO;
import codehows.dream.nutritionpirates.entity.Order;
import codehows.dream.nutritionpirates.entity.ProcessPlan;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.exception.NotFoundOrderException;
import codehows.dream.nutritionpirates.exception.NotFoundWorkPlanException;
import codehows.dream.nutritionpirates.repository.OrderRepository;
import codehows.dream.nutritionpirates.repository.StockRepository;
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
	private final ProgramTimeService programTimeService;
	private final StockRepository stockRepository;

	private int semiProduct = 0;
	private int totalSemiProduct = 0;
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

	public WorkPlanDetailDTO getWorkDetail(Long id) {
		Timestamp time = programTimeService.getProgramTime().getCurrentProgramTime();
		WorkPlan workPlan = workPlanRepository.findById(id).orElse(null);
		return WorkPlanDetailDTO.toWorkPlanDetailDTO(workPlan, time);
	}

	public RawBOMDTO calInputRaws(Order order) {
		return rawOrderInsertService.createRequirement(order);
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
					double localSemiProduct = 0;

					if (e.equals(Process.A1)) {
						if (order.getProduct() == ProductName.CABBAGE_JUICE) {
							workPlan = workPlans.createWorkPlan((int)Math.ceil(raws.getCabbage()));
						} else if (order.getProduct() == ProductName.BLACK_GARLIC_JUICE) {
							workPlan = workPlans.createWorkPlan((int)Math.ceil(raws.getGarlic()));
						}
						localSemiProduct = workPlan.getSemiProduct();
						totalSemiProduct += localSemiProduct;
						workPlan.setProcessPlan(processPlan);
						workPlanRepository.save(workPlan);
					} else if (e.equals(Process.A3)) {
						double semiCapa = totalSemiProduct;
						totalSemiProduct = 0;
						while (semiCapa > 0) {
							int capa = (int)Math.ceil(Math.min(semiCapa, Routing.EXTRACTION_ROUTING));
							workPlan = workPlans.createWorkPlan(capa);
							workPlan.setProcessPlan(processPlan);
							workPlanRepository.save(workPlan);
							semiCapa -= capa;
							localSemiProduct = workPlan.getSemiProduct();
							totalSemiProduct += localSemiProduct;
						}
					} else if (e.equals(Process.A4)) {
						double semiCapa = totalSemiProduct;
						totalSemiProduct = 0;
						while (semiCapa > 0) {
							int capa = (int)Math.ceil(Math.min(semiCapa, Routing.FILTER_ROUTING));
							workPlan = workPlans.createWorkPlan(capa);
							workPlan.setProcessPlan(processPlan);
							workPlanRepository.save(workPlan);
							semiCapa -= capa;
							localSemiProduct = workPlan.getSemiProduct();
							totalSemiProduct += localSemiProduct;
						}
					} else if (e.equals(Process.A5)) {
						double semiCapa = totalSemiProduct;
						totalSemiProduct = 0;
						while (semiCapa > 0) {
							int capa = (int)Math.ceil(Math.min(semiCapa, Routing.STERILIZATION_ROUTING));
							workPlan = workPlans.createWorkPlan(capa);
							workPlan.setProcessPlan(processPlan);
							workPlanRepository.save(workPlan);
							semiCapa -= capa;
							localSemiProduct = workPlan.getSemiProduct();
							totalSemiProduct += localSemiProduct;
						}
					} else if (e.equals(Process.A6)) {
						workPlan = workPlans.createWorkPlan((int)totalSemiProduct);
						workPlan.setProcessPlan(processPlan);
						workPlanRepository.save(workPlan);
						totalSemiProduct = workPlan.getSemiProduct();
					} else {
						workPlan = workPlans.createWorkPlan((int)totalSemiProduct);
						workPlan.setProcessPlan(processPlan);
						workPlanRepository.save(workPlan);
						totalSemiProduct = workPlan.getSemiProduct();
					}
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
					double localSemiProduct = 0;

					if (e.equals(Process.B1)) {
						if (order.getProduct() == ProductName.POMEGRANATE_JELLY_STICK) {
							workPlan = workPlans.createWorkPlan((int)Math.ceil(raws.getPomegranate()));
						} else if (order.getProduct() == ProductName.PLUM_JELLY_STICK) {
							workPlan = workPlans.createWorkPlan((int)Math.ceil(raws.getPlum()));
						}
						localSemiProduct = workPlan.getSemiProduct();
						totalSemiProduct += localSemiProduct;
						workPlan.setProcessPlan(processPlan);
						workPlanRepository.save(workPlan);
					} else if (e.equals(Process.B2)) {
						double semiCapa = totalSemiProduct;
						totalSemiProduct = 0;
						while (semiCapa > 0) {
							int capa = (int)Math.ceil(Math.min(semiCapa, Routing.MIX_ROUTING));
							workPlan = workPlans.createWorkPlan(capa);
							workPlan.setProcessPlan(processPlan);
							workPlanRepository.save(workPlan);
							semiCapa -= capa;
							localSemiProduct = workPlan.getSemiProduct();
							totalSemiProduct += localSemiProduct;
						}
					} else if (e.equals(Process.B4)) {
						double semiCapa = totalSemiProduct;
						totalSemiProduct = 0;
						while (semiCapa > 0) {
							int capa = (int)Math.ceil(Math.min(semiCapa, 100));
							workPlan = workPlans.createWorkPlan(capa);
							workPlan.setProcessPlan(processPlan);
							workPlanRepository.save(workPlan);
							semiCapa -= capa;
							localSemiProduct = workPlan.getSemiProduct();
							totalSemiProduct += localSemiProduct;
						}
					} else {
						workPlan = workPlans.createWorkPlan((int)totalSemiProduct);
						workPlan.setProcessPlan(processPlan);
						workPlanRepository.save(workPlan);
						totalSemiProduct = workPlan.getSemiProduct();
					}
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

	public List<WorkPlan> getActivateFacility(Facility facility) {
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
				case filter -> facilityDTO.setFilter(workPlans);
				case freeze -> facilityDTO.setFreeze(workPlans);
				case weighing -> facilityDTO.setWeighing(workPlans);

			}
		}

		return facilityDTO;
	}

	public WorkPlanDTO executeWork(Long id, String worker) {
		WorkPlan workPlan = workPlanRepository.findById(id).orElse(null);
		if (workPlan == null)
			throw new NotFoundWorkPlanException();
		WorkPlans work = getWorkPlanByProcess(workPlan.getProcess());
		if (work == null)
			throw new NotFoundWorkPlanException();
		workPlan.setWorker(worker);
		WorkPlan executedWork;
		executedWork = work.execute(workPlan);
		Process next = null;

		if (executedWork.getProcess().toString().contains("A")) {
			next = checkNextAProcess(executedWork.getProcess()).orElse(null);
		} else {
			next = checkNextBProcess(executedWork.getProcess()).orElse(null);
		}
		List<WorkPlan> list = getFacilities(next);
		WorkPlan nextWorkplan = workPlanRepository.findByProcessPlanIdAndProcess(
				executedWork.getProcessPlan().getId(), next);

		checkNextWorkPlan(nextWorkplan, list);

		// 1. 2개가 전부 작동 list.size == 2
		// 2. 1번만 작동 list = null
		// 3. 2번만 작동 list = 1

		// 설비 상태 조회

		return WorkPlanDTO.builder()
				.id(executedWork.getId())
				.endTime(executedWork.getEndTime() != null ? executedWork.getEndTime().toString() : null)
				.startTime(executedWork.getStartTime().toString())
				.lotCodes(executedWork.getLotCode() != null ? executedWork.getLotCode().getLotCode() : null)
				.facility(executedWork.getFacility())
				.rawsCodes(executedWork.getRawsCodes())
				.process(executedWork.getProcess())
				.build();
	}

	public void checkNextWorkPlan(WorkPlan nextWorkplan, List<WorkPlan> list) {
		if (nextWorkplan != null) {
			// 두 기계가 모두 작동 중이면 작업 실행 안 함
			if (list == null) {
				nextWorkplan.setActivate(true);
				return;
			}
			if (list.size() == 2) {
				nextWorkplan.setActivate(false);
			}

			// 한 기계만 작동 중이면 반대 기계를 작동
			else if (list.size() == 1) {
				Facility currentFacility = list.get(0).getFacility();
				Facility oppositeFacility = getOppositeFacility(currentFacility);

				if (oppositeFacility != null) {
					nextWorkplan.setFacility(oppositeFacility);
					nextWorkplan.setActivate(true);
				}
			} else {
				nextWorkplan.setActivate(true);
			}

			workPlanRepository.save(nextWorkplan);
		}
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
			return getFacilityAfterTreatmentStatus();
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
		WorkPlan plan = workPlanRepository.findByFacilityStatusAndFacility(FacilityStatus.AFTER_TREATMENT, facility);
		if (plan == null) {
			return workPlanRepository.findByFacilityStatusAndFacility(FacilityStatus.WORKING, facility);
		}
		return plan;
	}

	private List<WorkPlan> getFacilityStatus(Facility facility1, Facility facility2) {
		ArrayList<WorkPlan> list = new ArrayList<>();
		WorkPlan plan = getFacilityWorking(facility1);
		WorkPlan plans = getFacilityWorking(facility2);
		//둘 다 작동
		if (plan != null && plans != null) {
			list.add(0, plan);
			list.add(1, plans);
		}
		//1번기계 작동
		if (plans != null) {
			list.add(0, plans);
		}
		//1번기계 작동
		if (plan != null) {
			list.add(0, plan);
		}

		return list;
	}

	private List<WorkPlan> getFacilityAfterTreatmentStatus() {
		ArrayList<WorkPlan> list = new ArrayList<>();
		WorkPlan plan = getFacilityAfterTreatment(Facility.extractor1);
		WorkPlan plan1 = getFacilityAfterTreatment(Facility.extractor2);

		WorkPlan plans = getFacilityWorking(Facility.extractor1);
		WorkPlan plans1 = getFacilityWorking(Facility.extractor2);

		if (calTime(plan)) {
			plan.setFacilityStatus(FacilityStatus.STANDBY);
			workPlanRepository.save(plan);
		} else if (calTime(plans)) {
			plans.setFacilityStatus(FacilityStatus.STANDBY);
			workPlanRepository.save(plans);
		}

		if (plan1 != null && plans1 != null) {
			list.add(0, plan);
			list.add(1, plans);
		}
		if (plans1 != null) {
			list.add(0, plans1);
		}
		return list;
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
		Timestamp time = programTimeService.getProgramTime().getCurrentProgramTime();
		if (workPlan == null) {
			return false;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(workPlan.getEndTime());
		calendar.add(Calendar.DAY_OF_YEAR, 1);

		Timestamp oneDayAfterEndTime = new Timestamp(calendar.getTime().getTime());

		boolean isAfterOneDay = time.after(oneDayAfterEndTime);
		System.out.println("Is End Time after One Day After End Time? " + isAfterOneDay);

		return isAfterOneDay;
	}

	private Facility getOppositeFacility(Facility currentFacility) {
		return switch (currentFacility) {
			case juiceMachine1 -> Facility.juiceMachine2;
			case juiceMachine2 -> Facility.juiceMachine1;
			case StickMachine1 -> Facility.StickMachine2;
			case StickMachine2 -> Facility.StickMachine1;
			case extractor1 -> Facility.extractor2;
			case extractor2 -> Facility.extractor1;
			case sterilizer1 -> Facility.sterilizer2;
			case sterilizer2 -> Facility.sterilizer1;
			// 필요한 경우 다른 기계도 추가
			default -> null;
		};
	}

	public Page<WorkPlanListDTO> getWorkPlanData(Pageable pageable) {
		Page<WorkPlan> pages = workPlanRepository.findByEndTimeExists(pageable);
		return pages.map(e -> WorkPlanListDTO.builder()
				.work_plan_id(e.getId())
				.lotCode(e.getLotCode() == null ? null : e.getLotCode().getLotCode())
				.worker(e.getWorker())
				.process(e.getProcess())
				.startTime(e.getStartTime().toLocalDateTime().toString())
				.endTime(e.getEndTime().toLocalDateTime().toString())
				.build());
	}

	public Workbook getHistory() {
		List<WorkPlan> list = workPlanRepository.findByEndTimeExists();
		String time = programTimeService.getProgramTime()
				.getCurrentProgramTime()
				.toLocalDateTime()
				.toLocalDate()
				.toString();

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet(time + "완료된 작업 지시");

		Row headerRow = sheet.createRow(0);
		String[] headers = new String[] {"작업지시 번호", "LOT코드", "작업자", "공정", "시작시간", "종료시간"};

		for (int i = 0; i < headers.length; i++) {
			headerRow.createCell(i).setCellValue(headers[i]);
		}

		for (int i = 1; i < list.size() + 1; i++) {
			Row row = sheet.createRow(i);
			WorkPlan workPlan = list.get(i - 1);
			row.createCell(0).setCellValue(workPlan.getId());
			row.createCell(1).setCellValue(workPlan.getLotCode() == null ? "없음" : workPlan.getLotCode().getLotCode());
			row.createCell(2).setCellValue(workPlan.getWorker());
			row.createCell(3).setCellValue(workPlan.getProcess().toString());
			row.createCell(4).setCellValue(workPlan.getStartTime().toLocalDateTime().toString());
			row.createCell(5).setCellValue(workPlan.getEndTime().toLocalDateTime().toString());
		}

		return workbook;
	}
}
