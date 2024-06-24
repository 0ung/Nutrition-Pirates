package codehows.dream.nutritionpirates.service;

import codehows.dream.nutritionpirates.constants.ProductName;
import codehows.dream.nutritionpirates.entity.Order;
import codehows.dream.nutritionpirates.entity.ProcessPlan;
import codehows.dream.nutritionpirates.exception.NoFoundProductNameException;
import codehows.dream.nutritionpirates.repository.ProcessPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProcessPlanService {

    private final ProcessPlanRepository processPlanRepository;
    private final WorkPlanService workPlanService;

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

    public void updateWorkPlan(ProcessPlan processPlan) {

    }

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
