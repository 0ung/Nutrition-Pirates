package codehows.dream.nutritionpirates.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.FacilityStatus;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public interface WorkPlanRepository extends JpaRepository<WorkPlan, Long> {
	List<WorkPlan> findAllByProcessPlanId(Long id);

	List<WorkPlan> findByFacility(Facility facility);

	List<WorkPlan> findByFacilityAndActivateFalse(Facility facility);

	WorkPlan findByProcessPlanIdAndFacility(Long processPlanId, Facility facility);

	List<WorkPlan> findByFacilityStatus(FacilityStatus facilityStatus);

	WorkPlan findByFacilityStatusAndFacility(FacilityStatus facilityStatus, Facility facility);

	WorkPlan findByProcessPlanIdAndProcess(Long processPlanId, Process process);
}
