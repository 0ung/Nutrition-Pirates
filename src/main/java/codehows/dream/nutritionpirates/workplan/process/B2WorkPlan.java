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
public class B2WorkPlan implements WorkPlans {

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
		RawBOMDTO rawBOMDTO = getBom(processPlan);

		String[] rawsCodes = null;
		if (workPlan.getProcessPlan().getOrder().getProduct() == ProductName.PLUM_JELLY_STICK) {
			rawsCodes = getRawsCodes((int)rawBOMDTO.getPlum()
				, (int)rawBOMDTO.getCollagen(), workPlan.getProcessPlan().getOrder().getProduct());

			plan.setCapacity(calCapacity((int)rawBOMDTO.getPlum()));
		} else {
			rawsCodes = getRawsCodes((int)rawBOMDTO.getPomegranate()
				, (int)rawBOMDTO.getCollagen(), workPlan.getProcessPlan().getOrder().getProduct());

			plan.setCapacity(calCapacity((int)rawBOMDTO.getPomegranate()));
		}
		plan.setRawsCodes(Arrays.toString(rawsCodes));
		plan.setLotCode(lotCode);
		workPlanRepository.save(plan);
		return workPlan;
	}

	@Override
	public WorkPlan createWorkPlan(int input) {

		return WorkPlan.builder()
			.facility(Facility.mixer)
			.process(Process.B2)
			.processCompletionTime(expectTime(input))
			.semiProduct(input)
			.facilityStatus(FacilityStatus.STANDBY)
			.build();

	}

	public Timestamp expectTime(int input) {
		double time = WORK_PLAN_DURATION.mixDuration(input) + WORK_PLAN_DURATION.mixWaiting(input);
		int minToAdd = (int)time * 60;
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expectedTime = now.plusMinutes(minToAdd);
		return Timestamp.valueOf(expectedTime);
	}

	public Timestamp getComplete(Timestamp timestamp, int input) {
		double time = WORK_PLAN_DURATION.mixDuration(input) + Routing.MIX_WAITING_TIME;
		int minToAdd = (int)time * 60;
		LocalDateTime localDateTime = timestamp.toLocalDateTime();
		LocalDateTime completeTime = localDateTime.plusMinutes(minToAdd);
		return Timestamp.valueOf(completeTime);
	}

	public LotCode getLotCode(WorkPlan workPlan, Timestamp time) {
		String lotCode = CommonMethod.getLotCode(workPlan, time);
		return new LotCode(lotCode);
	}

	public int calCapacity(int input) {
		return input / Routing.MIX_ROUTING * 100;
	}

	public RawBOMDTO getBom(ProcessPlan processPlan) {
		Order order = orderRepository.findById(processPlan.getOrder().getId()).orElse(null);
		if (order == null) {
			throw new NotFoundOrderException();
		}
		return rawOrderInsertService.createRequirement(order);
	}

	public String[] getRawsCodes(int mixerInput, int collagenInput, ProductName productName) {
		// 원자재 재고
		int mixerRawCnt = stockMixerData(productName);
		int collagenRawCnt = stockCollagenData();
		if (mixerRawCnt < mixerInput || collagenRawCnt < collagenInput) {
			throw new NotEnoughRawsException();
		}

		List<Raws> honeyRaw = rawRepository.findByProduct(RawProductName.HONEY);
		List<Raws> wrappingRaw = rawRepository.findByProduct(RawProductName.WRAPPING_PAPER);

		List<Raws> availableHoneyRaws = getAvailableRaws(honeyRaw);
		List<Raws> availableWrappingRaws = getAvailableRaws(wrappingRaw);

		List<String> updatedCodes = new ArrayList<>();
		updatedCodes.addAll(updateRawsCodes(mixerInput, availableHoneyRaws));
		updatedCodes.addAll(updateRawsCodes(collagenInput, availableWrappingRaws));

		return updatedCodes.toArray(new String[0]);
	}

	private List<Raws> getAvailableRaws(List<Raws> raws) {
		return raws.stream().filter(e -> {
			int data = e.getRawsCode().contains("T") ? parseAmount(e.getRawsCode()) : 0;
			int total = parseTotal(e.getRawsCode());
			return (total - data) > 0;
		}).toList();
	}

	private List<String> updateRawsCodes(int input, List<Raws> availableRaws) {
		List<String> updatedCodes = new ArrayList<>();
		int remainingInput = input;

		for (Raws raw : availableRaws) {
			if (remainingInput <= 0) {
				break;
			}

			int data = raw.getRawsCode().contains("T") ? parseAmount(raw.getRawsCode()) : 0;
			int total = parseTotal(raw.getRawsCode());
			int availableAmount = total - data;

			if (availableAmount > 0) {
				int usedAmount = Math.min(remainingInput, availableAmount);
				remainingInput -= usedAmount;
				int newData = data + usedAmount;

				int tIndex = raw.getRawsCode().indexOf('T');
				String baseCode = (tIndex == -1) ? raw.getRawsCode() : raw.getRawsCode().substring(0, tIndex);
				String newCode = baseCode + "T" + newData;
				updatedCodes.add(newCode);

				// 원자재 코드 업데이트 후 저장
				raw.setRawsCode(newCode);
				rawRepository.save(raw);
			}
		}

		input = remainingInput;
		if (input > 0) {
			throw new NotEnoughRawsException();
		}
		return updatedCodes;
	}

	//20240621C2000T
	// 총량 부분을 파싱하는 메서드
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

	public int stockMixerData(ProductName productName) {
		List<RawShowGraphDTO> list = rawOrderInsertService.getRawStockGraph();
		int quantity = 0;
		if (productName == ProductName.PLUM_JELLY_STICK) {
			quantity = list.stream().mapToInt(e -> {
				if (e.getProduct().equals("매실(농축액"))
					return e.getQuantity();
				return 0;
			}).sum();
		} else if (productName == ProductName.POMEGRANATE_JELLY_STICK) {
			quantity = list.stream().mapToInt(e -> {
				if (e.getProduct().equals("석류(농축액)"))
					return e.getQuantity();
				return 0;
			}).sum();
		}
		return quantity;
	}

	public int stockCollagenData() {
		List<RawShowGraphDTO> list = rawOrderInsertService.getRawStockGraph();
		int quantity = 0;
		quantity = list.stream().mapToInt(e -> {
			if (e.getProduct().equals("콜라겐"))
				return e.getQuantity();
			return 0;
		}).sum();
		return quantity;
	}
}
