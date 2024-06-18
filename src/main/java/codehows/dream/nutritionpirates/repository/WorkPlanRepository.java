package codehows.dream.nutritionpirates.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.entity.WorkPlan;

public interface WorkPlanRepository extends JpaRepository<WorkPlan, Long> {
	List<WorkPlan> findAllByProcessPlanId(Long id);

	List<WorkPlan> findByFacility(Facility facility);

	List<WorkPlan> findByFacilityAndActivateTrue(Facility facility);
}
