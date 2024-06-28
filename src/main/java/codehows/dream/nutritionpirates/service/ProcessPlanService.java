package codehows.dream.nutritionpirates.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codehows.dream.nutritionpirates.constants.FacilityStatus;
import codehows.dream.nutritionpirates.constants.ProductName;
import codehows.dream.nutritionpirates.constants.Routing;
import codehows.dream.nutritionpirates.dto.RawBOMDTO;
import codehows.dream.nutritionpirates.entity.Order;
import codehows.dream.nutritionpirates.entity.ProcessPlan;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.exception.NoFoundProductNameException;
import codehows.dream.nutritionpirates.exception.NotFoundWorkPlanException;
import codehows.dream.nutritionpirates.repository.ProcessPlanRepository;
import codehows.dream.nutritionpirates.repository.WorkPlanRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProcessPlanService {

	private final ProcessPlanRepository processPlanRepository;
	private final WorkPlanService workPlanService;
	private final WorkPlanRepository workPlanRepository;
	private final RawOrderInsertService rawOrderInsertService;

	public void createProcessPlan(Order order) {
		ProcessPlan processPlan = processPlanRepository.save(ProcessPlan.builder()
			.order(order)
			.build());
		//작업지시 생성
		createWorkPlan(processPlan, order.getProduct());
	}

	public void createWorkPlan(ProcessPlan processPlan, ProductName productName) {
		switch (productName) {
			case BLACK_GARLIC_JUICE -> blackGarlicProcess(processPlan);
			case CABBAGE_JUICE -> cabbageProcess(processPlan);
			case POMEGRANATE_JELLY_STICK -> pomeProcess(processPlan);
			case PLUM_JELLY_STICK -> plumProcess(processPlan);
			default -> throw new NoFoundProductNameException();
		}
	}

	// capa 계산 된 공정 확인 후 부족하면
	//


	private void cabbageProcess(ProcessPlan processPlan) {
		workPlanService.createJuiceProcessPlan(processPlan);
	}

	private void blackGarlicProcess(ProcessPlan processPlan) {
		workPlanService.createJuiceProcessPlan(processPlan);
	}

	private void pomeProcess(ProcessPlan processPlan) {
		workPlanService.createStickProcessPlan(processPlan);
	}

	private void plumProcess(ProcessPlan processPlan) {
		workPlanService.createStickProcessPlan(processPlan);
	}
}
