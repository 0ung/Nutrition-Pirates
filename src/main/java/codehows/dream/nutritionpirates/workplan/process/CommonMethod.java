package codehows.dream.nutritionpirates.workplan.process;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.FacilityStatus;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.workplan.WorkPlanDuration;

public class CommonMethod {

	private final WorkPlanDuration workPlanDuration = new WorkPlanDuration();

	public static String getString(double time) {
		int hours = (int)time; // 시간 부분
		int minutes = (int)Math.ceil((time - hours) * 60); // 올림 처리한 분 부분
		if (minutes == 60) {
			hours += 1;
			minutes = 0;
		}
		return hours + "시 " + minutes + "분";
	}

	public static WorkPlan setTime(WorkPlan plan, Timestamp time, Timestamp processComplete) {

		if (plan.getStartTime() == null) {
			plan.setStartTime(time);
			//@TODO 이거변수 처리해야됨
			plan.setWorker("김영웅");
			plan.setSemiProduct(1000);
			plan.setFacilityStatus(FacilityStatus.WORKING);
			plan.setProcessCompletionTime(processComplete);
		} else {
			plan.setEndTime(time);
			plan.setFacilityStatus(FacilityStatus.WAITING);
		}
		return plan;
	}

	public static String getLotCode(WorkPlan workPlan, Timestamp time) {
		Process process = workPlan.getProcess();
		Facility facility = workPlan.getFacility();
		LocalDateTime localDateTime = time.toLocalDateTime();
		String formattedDate = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		return formattedDate + process + facility.getValue();
	}

}
