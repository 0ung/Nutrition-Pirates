package codehows.dream.nutritionpirates.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.FacilityStatus;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;

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
	// List<WorkPlan> findByEndTimeEmptyAndFacilityStatus(FacilityStatus facilityStatus);
}
