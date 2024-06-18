package codehows.dream.nutritionpirates.dto;

import java.util.List;

import codehows.dream.nutritionpirates.entity.WorkPlan;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ActivateFacilityDTO {
	private List<WorkPlan> juiceMachine1;
	private List<WorkPlan> juiceMachine2;
	private List<WorkPlan> StickMachine1;
	private List<WorkPlan> StickMachine2;
	private List<WorkPlan> extractor1;
	private List<WorkPlan> extractor2;
	private List<WorkPlan> sterilizer1;
	private List<WorkPlan> sterilizer2;
	private List<WorkPlan> mixer;
	private List<WorkPlan> filter;
	private List<WorkPlan> boxMachine;
	private List<WorkPlan> metalDetector;
	private List<WorkPlan> washer;
}
