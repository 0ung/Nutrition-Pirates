package codehows.dream.nutritionpirates.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.dto.ActivateFacilityDTO;
import codehows.dream.nutritionpirates.entity.Order;
import codehows.dream.nutritionpirates.entity.ProcessPlan;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.exception.NotFoundOrderException;
import codehows.dream.nutritionpirates.repository.OrderRepository;
import codehows.dream.nutritionpirates.repository.WorkPlanRepository;
import codehows.dream.nutritionpirates.workplan.WorkPlanFactoryProvider;
import codehows.dream.nutritionpirates.workplan.process.WorkPlans;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkPlanService {

	private final WorkPlanRepository workPlanRepository;
	private final OrderRepository orderRepository;

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
					semiProduct = Integer.parseInt(workPlan.getSemiProduct());
				} else if (e.equals(Process.A3)) {
					workPlan = workPlans.createWorkPlan(semiProduct);
					semiProduct = Integer.parseInt(workPlan.getSemiProduct());
				} else if (e.equals(Process.A4)) {
					workPlan = workPlans.createWorkPlan(semiProduct);
					semiProduct = Integer.parseInt(workPlan.getSemiProduct());
				} else if (e.equals(Process.A6)) {
					workPlan = workPlans.createWorkPlan(semiProduct);
					semiProduct = Integer.parseInt(workPlan.getSemiProduct());
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
					semiProduct = Integer.parseInt(workPlan.getSemiProduct());
				} else if (e.equals(Process.B4)) {
					workPlan = workPlans.createWorkPlan(semiProduct);
					semiProduct = Integer.parseInt(workPlan.getSemiProduct());
				} else {
					workPlan = workPlans.createWorkPlan(semiProduct);
				}

				workPlan.setProcessPlan(processPlan);
				workPlanRepository.save(workPlan);
			}
		);
	}

	public String expectDeliveryDate(ProcessPlan processPlan) {
		List<WorkPlan> list = workPlanRepository
			.findAllByProcessPlanId(processPlan.getId());
		int deliveryTime = list.stream().mapToInt(workPlan ->
		{
			if (workPlan.getProcessCompletionTime() == null) {
				return 0;
			}
			return parseTime(workPlan.getProcessCompletionTime());
		}).sum();

		int quo = deliveryTime / 60;
		int remain = deliveryTime % 60;
		return quo + "시 " + remain + "분";
	}

	private int parseTime(String time) {
		String[] strings = time.split(" ");
		if (strings.length > 1) {
			int hour = Integer.parseInt(strings[0].replaceAll("[^0-9]", ""));
			int min = Integer.parseInt(strings[1].replaceAll("[^0-9]", ""));
			return hour * 60 + min;
		}
		return Integer.parseInt(strings[0].replaceAll("[^0-9]", ""));
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
			List<WorkPlan> workPlans = activateFacility(facility);
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

}
