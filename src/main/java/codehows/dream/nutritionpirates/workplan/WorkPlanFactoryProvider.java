package codehows.dream.nutritionpirates.workplan;

import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.workplan.process.*;

import java.util.HashMap;
import java.util.Map;

public class WorkPlanFactoryProvider {
    private static final Map<Process, Class<? extends WorkPlans>> factoryMap = new HashMap<>();

        static {
            factoryMap.put(Process.A1, A1WorkPlan.class);
            factoryMap.put(Process.A2, A2WorkPlan.class);
            factoryMap.put(Process.A3, A3WorkPlan.class);
            factoryMap.put(Process.A4, A4WorkPlan.class);
            factoryMap.put(Process.A5, A5WorkPlan.class);
            factoryMap.put(Process.A6, A6WorkPlan.class);
            factoryMap.put(Process.A7, A7WorkPlan.class);
            factoryMap.put(Process.A8, A8WorkPlan.class);
            factoryMap.put(Process.B1, B1WorkPlan.class);
            factoryMap.put(Process.B2, B2WorkPlan.class);
            factoryMap.put(Process.B3, B3WorkPlan.class);
            factoryMap.put(Process.B4, B4WorkPlan.class);
            factoryMap.put(Process.B5, B5WorkPlan.class);
            factoryMap.put(Process.B6, B6WorkPlan.class);
            factoryMap.put(Process.B7, B7WorkPlan.class);
        }


    public static WorkPlans createWorkOrder(Process workOrderType) {
        Class<? extends WorkPlans> workOrderClass = factoryMap.get(workOrderType);
        if (workOrderClass != null) {
            try {
                return workOrderClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new IllegalArgumentException("Unknown work order type: " + workOrderType);
    }
}
