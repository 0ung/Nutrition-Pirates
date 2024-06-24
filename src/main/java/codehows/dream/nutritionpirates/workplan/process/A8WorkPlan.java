package codehows.dream.nutritionpirates.workplan.process;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.FacilityStatus;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.constants.RawProductName;
import codehows.dream.nutritionpirates.constants.Routing;
import codehows.dream.nutritionpirates.dto.RawBOMDTO;
import codehows.dream.nutritionpirates.dto.RawShowGraphDTO;
import codehows.dream.nutritionpirates.entity.LotCode;
import codehows.dream.nutritionpirates.entity.Order;
import codehows.dream.nutritionpirates.entity.ProcessPlan;
import codehows.dream.nutritionpirates.entity.Raws;
import codehows.dream.nutritionpirates.entity.Stock;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.exception.NotEnoughRawsException;
import codehows.dream.nutritionpirates.exception.NotFoundOrderException;
import codehows.dream.nutritionpirates.exception.NotFoundWorkPlanException;
import codehows.dream.nutritionpirates.repository.LotCodeRepository;
import codehows.dream.nutritionpirates.repository.OrderRepository;
import codehows.dream.nutritionpirates.repository.ProcessPlanRepository;
import codehows.dream.nutritionpirates.repository.RawRepository;
import codehows.dream.nutritionpirates.repository.StockRepository;
import codehows.dream.nutritionpirates.repository.WorkPlanRepository;
import codehows.dream.nutritionpirates.service.ProgramTimeService;
import codehows.dream.nutritionpirates.service.RawOrderInsertService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class A8WorkPlan implements WorkPlans {

	private final ProgramTimeService programTimeService;
	private final WorkPlanRepository workPlanRepository;
	private final LotCodeRepository lotCodeRepository;
	private final RawRepository rawRepository;
	private final RawOrderInsertService rawOrderInsertService;
	private final ProcessPlanRepository processPlanRepository;
	private final OrderRepository orderRepository;
	private final StockRepository stockRepository;

	@Override
	public WorkPlan execute(WorkPlan workPlan) {
		Timestamp time = programTimeService.getProgramTime().getCurrentProgramTime();
		Timestamp comTime = getComplete(time, workPlan.getSemiProduct());
		WorkPlan plan = CommonMethod.setTime(workPlan, time, comTime);
		if (workPlan.getEndTime() != null) {
			workPlanRepository.save(plan);
			stockRepository.save(Stock.builder()
					.workPlan(workPlan)
					.quantity(workPlan.getSemiProduct())
					.createDate(Date.valueOf(time.toLocalDateTime().toLocalDate()))
				.build());
			return workPlan;
		}
		LotCode lotCode = getLotCode(workPlan, time);
		lotCodeRepository.saveAndFlush(lotCode);
		ProcessPlan processPlan = processPlanRepository.findById(workPlan.getProcessPlan().getId()).orElse(null);

		if (processPlan == null) {
			throw new NotFoundWorkPlanException();
		}

		int input = getBom(processPlan);

		String[] rawsCodes = getRawsCodes(input);

		plan.setLotCode(lotCode);
		plan.setCapacity(calCapacity(workPlan.getSemiProduct()));
		plan.setRawsCodes(Arrays.toString(rawsCodes));
		workPlanRepository.save(plan);
		return workPlan;
	}

	@Override
	public WorkPlan createWorkPlan(int input) {
		int data = process(input);
		return WorkPlan.builder()
			.facility(Facility.boxMachine)
			.process(Process.A8)
			.processCompletionTime(expectTime(data))
			.semiProduct(data)
			.facilityStatus(FacilityStatus.STANDBY)
			.build();
	}

	public Timestamp expectTime(int input) {
		double time = WORK_PLAN_DURATION.boxPackingDuration(input) + WORK_PLAN_DURATION.boxPackingWaiting(input);
		int minToAdd = (int)time * 60;
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expectedTime = now.plusMinutes(minToAdd);
		return Timestamp.valueOf(expectedTime);
	}

	public Timestamp getComplete(Timestamp timestamp, int input) {
		double time = WORK_PLAN_DURATION.boxPackingDuration(input) + Routing.BOX_PACKING_WAITING_TIME;
		int minToAdd = (int)time * 60;
		LocalDateTime localDateTime = timestamp.toLocalDateTime();
		LocalDateTime completeTime = localDateTime.plusMinutes(minToAdd);
		return Timestamp.valueOf(completeTime);
	}

	public int process(int input) {
		return (int)Math.ceil(input / 30.0);

	}

	public LotCode getLotCode(WorkPlan workPlan, Timestamp time) {
		String lotCode = CommonMethod.getLotCode(workPlan, time);
		WorkPlan preWorkPlan = workPlanRepository.findByProcessPlanIdAndFacility(workPlan.getProcessPlan().getId(),
			Facility.juiceMachine1);
		if (preWorkPlan == null) {
			preWorkPlan = workPlanRepository.findByProcessPlanIdAndFacility(workPlan.getProcessPlan().getId(),
				Facility.juiceMachine2);
		}
		if (preWorkPlan == null) {
			throw new NotFoundWorkPlanException();
		}
		String preLotCode = preWorkPlan.getLotCode().getLetCode();

		return new LotCode(lotCode, preLotCode);
	}

	public int calCapacity(int input) {
		return input / Routing.BOX_PACKING_ROUTING * 100;
	}

	public String[] getRawsCodes(int input) {
		//원자재 재고
		int stackRaws = stockData();
		if (stackRaws < input) {
			throw new NotEnoughRawsException();
		}

		List<Raws> raws = rawRepository.findByProduct(RawProductName.BOX);

		List<Raws> availableRaws = raws.stream().filter(e -> {
			int data = 0;
			if (e.getRawsCode().contains("T")) {
				data = parseAmount(e.getRawsCode());
			}
			int total = parseTotal(e.getRawsCode());
			return (total - data) > 0;
		}).toList();

		List<String> updatedCodes = new ArrayList<>();
		int remainingInput = input;

		for (Raws raw : availableRaws) {
			if (remainingInput <= 0) {
				break;
			}

			int data = 0;
			if (raw.getRawsCode().contains("T")) {
				data = parseAmount(raw.getRawsCode());
			}
			int total = parseTotal(raw.getRawsCode());
			int availableAmount = total - data;

			if (availableAmount > 0) {
				int usedAmount = Math.min(remainingInput, availableAmount);
				remainingInput -= usedAmount;
				int newData = data + usedAmount;

				// 원자재 코드 업데이트
				String newCode = raw.getRawsCode().substring(0, 13) + "T" + newData;
				updatedCodes.add(newCode);

				// 원자재 코드 업데이트 후 저장
				raw.setRawsCode(newCode);
				rawRepository.save(raw);
			}
		}

		if (remainingInput > 0) {
			throw new NotEnoughRawsException();
		}
		return updatedCodes.toArray(new String[0]);
	}

	//20240621C2000T
	public static int parseTotal(String codes) {
		// 날짜 (8자리) + 원자재 식별자 (1자리) 이후부터 'T' 전까지가 총량
		int endIndex = codes.indexOf('T');
		if (endIndex == -1) {
			endIndex = codes.length(); // 'T'가 없으면 끝까지
		}
		return Integer.parseInt(codes.substring(9, endIndex));
	}

	// 사용량 부분을 파싱하는 메서드 ('T' 이후 부분)
	public static int parseAmount(String codes) {
		int tIndex = codes.indexOf('T');
		if (tIndex == -1) {
			return 0; // 'T'가 없으면 사용량은 0
		}
		return Integer.parseInt(codes.substring(tIndex + 1));
	}

	public int stockData() {
		List<RawShowGraphDTO> list = rawOrderInsertService.getRawStockGraph();
		return list.stream().mapToInt(e -> {
			if (e.getProduct().equals("박스"))
				return e.getQuantity();
			return 0;
		}).sum();
	}

	public int getBom(ProcessPlan processPlan) {
		Order order = orderRepository.findById(processPlan.getOrder().getId()).orElse(null);
		if (order == null) {
			throw new NotFoundOrderException();
		}
		RawBOMDTO rawBOMDTO = rawOrderInsertService.createRequirement(order);
		return (int)rawBOMDTO.getBox();
	}
}
