package codehows.dream.nutritionpirates.workplan.process;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.FacilityStatus;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.constants.ProductName;
import codehows.dream.nutritionpirates.constants.RawProductName;
import codehows.dream.nutritionpirates.constants.Routing;
import codehows.dream.nutritionpirates.dto.RawBOMDTO;
import codehows.dream.nutritionpirates.dto.RawShowGraphDTO;
import codehows.dream.nutritionpirates.entity.LotCode;
import codehows.dream.nutritionpirates.entity.Order;
import codehows.dream.nutritionpirates.entity.ProcessPlan;
import codehows.dream.nutritionpirates.entity.Raws;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.exception.NotEnoughRawsException;
import codehows.dream.nutritionpirates.exception.NotFoundOrderException;
import codehows.dream.nutritionpirates.exception.NotFoundWorkPlanException;
import codehows.dream.nutritionpirates.repository.LotCodeRepository;
import codehows.dream.nutritionpirates.repository.OrderRepository;
import codehows.dream.nutritionpirates.repository.ProcessPlanRepository;
import codehows.dream.nutritionpirates.repository.RawRepository;
import codehows.dream.nutritionpirates.repository.WorkPlanRepository;
import codehows.dream.nutritionpirates.service.ProgramTimeService;
import codehows.dream.nutritionpirates.service.RawOrderInsertService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class A1WorkPlan implements WorkPlans {

	private final ProgramTimeService programTimeService;
	private final WorkPlanRepository workPlanRepository;
	private final LotCodeRepository lotCodeRepository;
	private final RawRepository rawRepository;
	private final OrderRepository orderRepository;
	private final ProcessPlanRepository processPlanRepository;
	private final RawOrderInsertService rawOrderInsertService;

	@Override
	public WorkPlan execute(WorkPlan workPlan) {
		Timestamp time = programTimeService.getProgramTime().getCurrentProgramTime();
		Timestamp comTime = getComplete(time, workPlan.getSemiProduct());
		WorkPlan plan = CommonMethod.setTime(workPlan, time, comTime);
		if (workPlan.getEndTime() != null) {
			workPlanRepository.save(plan);
			return workPlan;
		}
		LotCode lotCode = getLotCode(workPlan, time);
		lotCodeRepository.saveAndFlush(lotCode);
		ProcessPlan processPlan = processPlanRepository.findById(workPlan.getProcessPlan().getId()).orElse(null);

		if (processPlan == null) {
			throw new NotFoundWorkPlanException();
		}

		int input = getBom(processPlan);

		String[] rawsCodes = getRawsCodes(input, workPlan.getProcessPlan().getOrder().getProduct());
		plan.setCapacity(calCapacity(input));
		plan.setRawsCodes(Arrays.toString(rawsCodes));

		plan.setLotCode(lotCode);
		workPlanRepository.save(plan);
		return workPlan;
	}

	@Override
	public WorkPlan createWorkPlan(int input) {

		return WorkPlan.builder()
			.facility(Facility.washer)
			.process(Process.A1)
			.processCompletionTime(expectTime(input))
			.semiProduct(process(input))
			.facilityStatus(FacilityStatus.STANDBY)
			.build();
	}

	public Timestamp expectTime(int input) {
		// 시간 계산
		double time = WORK_PLAN_DURATION.washingDuration(input);
		int minToAdd = (int)time * 60;

		// 현재 날짜와 시간 가져오기
		LocalDateTime now = LocalDateTime.now();

		// 분 추가
		LocalDateTime expectedTime = now.plusMinutes(minToAdd);

		// LocalDateTime을 Timestamp로 변환
		return Timestamp.valueOf(expectedTime);
	}

	public int process(int input) {
		return (int)Math.ceil(input * 0.75);
	}

	public Timestamp getComplete(Timestamp timestamp, int input) {
		double time = WORK_PLAN_DURATION.washingDuration(input);
		int minToAdd = (int)time * 60;

		// Timestamp를 LocalDateTime으로 변환
		LocalDateTime localDateTime = timestamp.toLocalDateTime();

		// 분 추가
		LocalDateTime completeTime = localDateTime.plusMinutes(minToAdd);

		// LocalDateTime을 Timestamp로 다시 변환하여 반환
		return Timestamp.valueOf(completeTime);
	}

	public LotCode getLotCode(WorkPlan workPlan, Timestamp time) {
		String lotCode = CommonMethod.getLotCode(workPlan, time);
		return new LotCode(lotCode);
	}

	// Method to get raw codes
	public String[] getRawsCodes(int input, ProductName productName) {
		//원자재 재고
		int stackRaws = stockData(productName);
		if (stackRaws < input) {
			throw new NotEnoughRawsException();
		}

		List<Raws> raws = rawRepository.findByProduct(
			productName == ProductName.CABBAGE_JUICE ? RawProductName.CABBAGE : RawProductName.BLACK_GARLIC);

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

	public int stockData(ProductName productName) {
		List<RawShowGraphDTO> list = rawOrderInsertService.getRawStockGraph();
		int quantity = 0;
		if (productName == ProductName.CABBAGE_JUICE) {
			quantity = list.stream().mapToInt(e -> {
				if (e.getProduct().equals("양배추"))
					return e.getQuantity();
				return 0;
			}).sum();
		} else if (productName == ProductName.BLACK_GARLIC_JUICE) {
			quantity = list.stream().mapToInt(e -> {
				if (e.getProduct().equals("흑마늘"))
					return e.getQuantity();
				return 0;
			}).sum();
		}
		return quantity;
	}

	public int getBom(ProcessPlan processPlan) {
		Order order = orderRepository.findById(processPlan.getOrder().getId()).orElse(null);
		if (order == null) {
			throw new NotFoundOrderException();
		}
		RawBOMDTO rawBOMDTO = rawOrderInsertService.createRequirement(order);
		if (order.getProduct() == ProductName.CABBAGE_JUICE) {
			return (int)rawBOMDTO.getCabbage();
		} else {
			return (int)rawBOMDTO.getGarlic();
		}
	}

	public int calCapacity(int input) {
		return input / Routing.WASHING_ROUTING_KG *100;
	}
}
