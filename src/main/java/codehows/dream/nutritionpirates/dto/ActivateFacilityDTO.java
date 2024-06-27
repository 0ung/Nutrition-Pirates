package codehows.dream.nutritionpirates.dto;

import java.util.List;

import codehows.dream.nutritionpirates.entity.WorkPlan;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ActivateFacilityDTO {
	private List<WorkPlanDTO> juiceMachine1;
	private List<WorkPlanDTO> juiceMachine2;
	private List<WorkPlanDTO> StickMachine1;
	private List<WorkPlanDTO> StickMachine2;
	private List<WorkPlanDTO> extractor1;
	private List<WorkPlanDTO> extractor2;
	private List<WorkPlanDTO> sterilizer1;
	private List<WorkPlanDTO> sterilizer2;
	private List<WorkPlanDTO> mixer;
	private List<WorkPlanDTO> filter;
	private List<WorkPlanDTO> boxMachine;
	private List<WorkPlanDTO> metalDetector;
	private List<WorkPlanDTO> washer;
	private List<WorkPlanDTO> weighing;
	private List<WorkPlanDTO> freeze;
}
