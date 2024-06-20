package codehows.dream.nutritionpirates.workplan.process;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.constants.ProductName;
import codehows.dream.nutritionpirates.constants.RawProductName;
import codehows.dream.nutritionpirates.dto.RawBOMDTO;
import codehows.dream.nutritionpirates.dto.RawShowGraphDTO;
import codehows.dream.nutritionpirates.entity.LotCode;
import codehows.dream.nutritionpirates.entity.Order;
import codehows.dream.nutritionpirates.entity.ProcessPlan;
import codehows.dream.nutritionpirates.entity.Raws;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.exception.NotEnoughRawsException;
import codehows.dream.nutritionpirates.exception.NotFoundWorkPlanException;
import codehows.dream.nutritionpirates.repository.LotCodeRepository;
import codehows.dream.nutritionpirates.repository.OrderRepository;
import codehows.dream.nutritionpirates.repository.ProcessPlanRepository;
import codehows.dream.nutritionpirates.repository.RawRepository;
import codehows.dream.nutritionpirates.repository.WorkPlanRepository;
import codehows.dream.nutritionpirates.service.BOMCalculatorService;
import codehows.dream.nutritionpirates.service.ProgramTimeService;
import codehows.dream.nutritionpirates.service.RawGraphService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class A1WorkPlan implements WorkPlans {

	private final ProgramTimeService programTimeService;
	private final WorkPlanRepository workPlanRepository;
	private final LotCodeRepository lotCodeRepository;
	private final BOMCalculatorService bomCalculatorService;
	private final RawGraphService rawGraphService;
	private final RawRepository rawRepository;
	private final OrderRepository orderRepository;
	private final ProcessPlanRepository processPlanRepository;

	@Override
	public WorkPlan execute(WorkPlan workPlan) {
		Timestamp time = programTimeService.getProgramTime().getCurrentProgramTime();
		Timestamp comTime = getComplete(time, workPlan.getSemiProduct());
		WorkPlan plan = CommonMethod.setTime(workPlan, time, comTime);
		LotCode lotCode = getLotCode(workPlan, time);
		lotCodeRepository.saveAndFlush(lotCode);
		ProcessPlan processPlan = processPlanRepository.findById(workPlan.getProcessPlan().getId()).orElse(null);

		if (processPlan == null) {
			throw new NotFoundWorkPlanException();
		}

		int input = getBom(processPlan);

		String[] rawsCodes = getRawsCodes(input, workPlan.getProcessPlan().getOrder().getProduct());

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
		List<Raws> availableRaws = raws.stream().filter(e -> parseAmount(e.getRawsCode())).collect(Collectors.toList());

		List<String> updatedCodes = new ArrayList<>();
		for (Raws raw : availableRaws) {
			int availableAmount = parseAvailable(raw.getRawsCode());

			if (availableAmount >= input) {
				String updatedCode = updateRawsCode(raw.getRawsCode(), input);
				updatedCodes.add(updatedCode);
				return updatedCodes.toArray(new String[0]);
			} else {
				String updatedCode = updateRawsCode(raw.getRawsCode(), availableAmount);
				updatedCodes.add(updatedCode);
				input -= availableAmount;
			}
		}

		throw new NotEnoughRawsException(); // If input amount cannot be fulfilled by available raws
	}

	// Method to parse product code
	public int parseProductCode(String codes) {
		return Integer.parseInt(codes.substring(8, 12));
	}

	// Method to check if there is enough amount available
	public boolean parseAmount(String codes) {
		if (codes.contains("T")) {
			int amount = Integer.parseInt(codes.substring(13));
			return amount > 0;
		}
		return false;
	}

	// Method to parse available amount from raw codes
	public int parseAvailable(String codes) {
		return Integer.parseInt(codes.substring(13));
	}

	// Method to update raw code with the new amount
	public String updateRawsCode(String codes, int input) {
		String base = codes.substring(0, 13); // Assuming 'T' is at position 12
		return base + input;
	}

	public int stockData(ProductName productName) {
		List<RawShowGraphDTO> list = rawGraphService.getRawStockGraph();
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
		RawBOMDTO rawBOMDTO = bomCalculatorService.createRequirement(order);

		return (int)rawBOMDTO.getIngredient1();
	}

}
