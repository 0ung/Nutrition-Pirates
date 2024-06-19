package codehows.dream.nutritionpirates.workplan;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import codehows.dream.nutritionpirates.constants.Process;
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

@Component
public class WorkPlanFactoryProvider implements ApplicationContextAware {

	private static ApplicationContext context;
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
				return context.getBean(workOrderClass);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		throw new IllegalArgumentException("Unknown work order type: " + workOrderType);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}
}
