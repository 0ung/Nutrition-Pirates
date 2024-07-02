package codehows.dream.nutritionpirates.repository;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.FacilityStatus;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.ProcessPlan;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface WorkPlanRepository extends JpaRepository<WorkPlan, Long> {

	List<WorkPlan> findAllByProcessPlanId(Long id);

	List<WorkPlan> findByFacility(Facility facility);

	WorkPlan findByProcessPlanIdAndFacility(Long processPlanId, Facility facility);

	WorkPlan findByFacilityStatusAndFacility(FacilityStatus facilityStatus, Facility facility);

	WorkPlan findByProcessPlanIdAndProcess(Long processPlanId, Process process);

	@Query("SELECT w FROM WorkPlan w where w.endTime IS NOT null ")
	Page<WorkPlan> findByEndTimeExists(Pageable pageable);

	@Query("SELECT w FROM WorkPlan w where w.endTime IS NOT null ")
	List<WorkPlan> findByEndTimeExists();

	List<WorkPlan> findByEndTimeBetween(Timestamp startTime, Timestamp endTime);

	@Query("SELECT w FROM WorkPlan w WHERE w.lotCode.id = :lotCodeId")
	WorkPlan findByLotCodeId(@Param("lotCodeId") String lotCodeId);

	List<WorkPlan> findByEndTimeIsNullAndFacilityStatusAndFacility(FacilityStatus facilityStatus, Facility facility);

	Page<WorkPlan> findByProcessInAndActivateTrue(List<String> processes, Pageable pageable);

	@Query(value = "SELECT * FROM work_plan wp WHERE DATE_FORMAT(wp.start_time, '%Y-%m-%d') LIKE CONCAT(:startTime, '%') AND wp.facility_status = :facilityStatus", nativeQuery = true)
	Page<WorkPlan> findByStartTimeAndFacilityStatus(@Param("startTime") String startTime,
		@Param("facilityStatus") String facilityStatus, Pageable pageable);

	@Query("SELECT wp FROM WorkPlan wp WHERE wp.endTime IS NULL AND wp.processPlan NOT IN :processPlans")
	List<WorkPlan> findByEndTimeIsNullAndProcessPlanNotIn(@Param("processPlans") List<ProcessPlan> processPlans);


}