package codehows.dream.nutritionpirates.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.FacilityStatus;
import codehows.dream.nutritionpirates.dto.FacilityOutPutDTO;
import codehows.dream.nutritionpirates.dto.FacilityStatusDTO;
import codehows.dream.nutritionpirates.dto.WorkPlanDetailDTO;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.repository.WorkPlanRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacilityService {

	private final WorkPlanRepository workPlanRepository;
	private final WorkPlanService workPlanService;
	private final ProgramTimeService programTimeService;
	//각 제품 별 가동상태, 진행률
	// , 세부 내용 표시

	public List<WorkPlan> getFacilityStatus(Facility facility) {
		List<WorkPlan> workPlans = workPlanRepository.findByEndTimeIsNullAndFacilityStatusAndFacility(
			FacilityStatus.WORKING, facility);
		List<WorkPlan> workPlans1 = workPlanRepository.findByEndTimeIsNullAndFacilityStatusAndFacility(
			FacilityStatus.AFTER_TREATMENT, facility);
		List<WorkPlan> result = new ArrayList<>();

		result.addAll(workPlans);
		result.addAll(workPlans1);

		return result;
	}

	//가동률
	//코드 변환
	public List<FacilityStatusDTO> getFacilityStatus() {
		HashMap<Facility, List<WorkPlan>> data = new HashMap<>();
		List<FacilityStatusDTO> list = new ArrayList<>();
		Arrays.stream(Facility.getAllFacilities()).forEach(
			e -> data.put(e, getFacilityStatus(e))
		);
		Facility[] facilities = Facility.getAllFacilities();

		Arrays.stream(facilities).forEach(e -> {
			FacilityStatusDTO facilityStatusDTO = new FacilityStatusDTO();
			if (data.get(e) != null) {
				data.get(e).forEach(workPlan -> {
					if (workPlan.getFacilityStatus() == FacilityStatus.WORKING) {
						facilityStatusDTO.setFacility(e);
						facilityStatusDTO.setFacilityStatus(FacilityStatus.WORKING);
						Timestamp time = programTimeService.getProgramTime().getCurrentProgramTime();
						facilityStatusDTO.setProcess(WorkPlanDetailDTO.calProcess(workPlan, time));
					} else if (workPlan.getFacilityStatus() == FacilityStatus.AFTER_TREATMENT) {
						facilityStatusDTO.setFacility(e);
						facilityStatusDTO.setFacilityStatus(FacilityStatus.AFTER_TREATMENT);
					} else {
						facilityStatusDTO.setFacility(e);
						facilityStatusDTO.setFacilityStatus(FacilityStatus.STANDBY);
					}
					facilityStatusDTO.setWorkPlanId(workPlan.getId());
				});
			} else {
				facilityStatusDTO.setFacility(e);
				facilityStatusDTO.setFacilityStatus(FacilityStatus.STANDBY);
			}
			facilityStatusDTO.setFacility(e);
			list.add(facilityStatusDTO);
		});
		return list;
	}

	// 각 설비의 생산량을 일/ 월별 조회
	public List<FacilityOutPutDTO> getDailyOutPut() {
		// 현재 프로그램 시간을 가져옴
		Timestamp timestamp = programTimeService.getProgramTime().getCurrentProgramTime();
		LocalDateTime currentDateTime = timestamp.toLocalDateTime();

		// 당일의 시작과 끝 시간을 구함
		LocalDate currentDate = currentDateTime.toLocalDate();
		LocalDateTime startOfDay = currentDate.atStartOfDay();
		LocalDateTime endOfDay = currentDate.atTime(23, 59, 59);

		// 해당 기간의 WorkPlan을 가져옴
		List<WorkPlan> workPlans = workPlanRepository.findByEndTimeBetween(Timestamp.valueOf(startOfDay),
			Timestamp.valueOf(endOfDay));

		// WorkPlan을 Facility별로 그룹화하고 생산량 계산
		return calculateFacilityOutput(workPlans);
	}

	public List<FacilityOutPutDTO> getMonthlyOutPut() {
		// 현재 프로그램 시간을 가져옴
		Timestamp timestamp = programTimeService.getProgramTime().getCurrentProgramTime();
		LocalDateTime currentDateTime = timestamp.toLocalDateTime();

		// 해당 월의 시작과 끝 시간을 구함
		YearMonth currentMonth = YearMonth.from(currentDateTime);
		LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
		LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);

		// 해당 기간의 WorkPlan을 가져옴
		List<WorkPlan> workPlans = workPlanRepository.findByEndTimeBetween(Timestamp.valueOf(startOfMonth),
			Timestamp.valueOf(endOfMonth));

		// WorkPlan을 Facility별로 그룹화하고 생산량 계산
		return calculateFacilityOutput(workPlans);
	}

	private List<FacilityOutPutDTO> calculateFacilityOutput(List<WorkPlan> workPlans) {
		// WorkPlan을 Facility별로 그룹화
		Map<Facility, List<WorkPlan>> groupedWorkPlans = workPlans.stream()
			.collect(Collectors.groupingBy(WorkPlan::getFacility));

		// 각 FacilityOutPutDTO 생성 및 리스트로 수집
		return groupedWorkPlans.entrySet().stream()
			.map(entry -> {
				Facility facility = entry.getKey();
				List<WorkPlan> facilityWorkPlans = entry.getValue();

				FacilityOutPutDTO outputDTO = new FacilityOutPutDTO();
				outputDTO.setFacility(facility);
				outputDTO.setOutput(facilityWorkPlans.stream().mapToInt(WorkPlan::getSemiProduct).sum());

				return outputDTO;
			})
			.collect(Collectors.toList());
	}

}
