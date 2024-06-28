package codehows.dream.nutritionpirates.workplan.process;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.FacilityStatus;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.workplan.WorkPlanDuration;

@Component
public class CommonMethod {

	public static WorkPlan setTime(WorkPlan plan, Timestamp time, Timestamp processComplete) {

		if (plan.getStartTime() == null) {
			plan.setStartTime(time);
			plan.setFacilityStatus(FacilityStatus.WORKING);
			plan.setProcessCompletionTime(processComplete);
		} else {
			plan.setEndTime(time);
			plan.setActivate(false);
			plan.setFacilityStatus(FacilityStatus.STANDBY);
		}
		return plan;
	}

	public static String getLotCode(WorkPlan workPlan, Timestamp time) {
		Process process = workPlan.getProcess();
		Facility facility = workPlan.getFacility();
		LocalDateTime localDateTime = time.toLocalDateTime();
		String formattedDate = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
		return formattedDate + process + facility.getValue();
	}

}
