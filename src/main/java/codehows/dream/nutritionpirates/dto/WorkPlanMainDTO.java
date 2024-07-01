package codehows.dream.nutritionpirates.dto;

import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkPlanMainDTO {
	private Long orderId;
	private Process process;
	private int semiProduct;

	public static WorkPlanMainDTO toWorkPlanDetailDTO(WorkPlan plan, Long orderId) {
		return WorkPlanMainDTO.builder()
			.orderId(orderId)
			.process(plan.getProcess())
			.semiProduct(plan.getSemiProduct())
			.build();
	}
}
