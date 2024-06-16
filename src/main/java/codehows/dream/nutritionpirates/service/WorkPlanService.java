package codehows.dream.nutritionpirates.service;

import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.ProcessPlan;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.repository.WorkPlanRepository;
import codehows.dream.nutritionpirates.workplan.WorkPlanDuration;
import codehows.dream.nutritionpirates.workplan.WorkPlanFactoryProvider;
import codehows.dream.nutritionpirates.workplan.WorkPlans;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkPlanService {

    private final WorkPlanRepository workPlanRepository;
    private final WorkPlanDuration workPlanDuration;
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
                    WorkPlan workPlan = workPlans.createWorkPlan();
                    workPlan.setProcessPlan(processPlan);
                    workPlanRepository.save(workPlan);
                }
        );
    }


    public void createStickProcessPlan(ProcessPlan processPlan) {
        stickProcess.forEach(
                (e) -> {
                    WorkPlans workPlans = WorkPlanFactoryProvider.createWorkOrder(e);
                    WorkPlan workPlan = workPlans.createWorkPlan();
                    workPlan.setProcessPlan(processPlan);
                    workPlanRepository.save(workPlan);
                }
        );
    }

}
