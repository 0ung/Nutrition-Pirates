package codehows.dream.nutritionpirates.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import codehows.dream.nutritionpirates.entity.ProcessPlan;

public interface ProcessPlanRepository extends JpaRepository<ProcessPlan, Long> {

    ProcessPlan findByOrderId(Long orderId);
}
