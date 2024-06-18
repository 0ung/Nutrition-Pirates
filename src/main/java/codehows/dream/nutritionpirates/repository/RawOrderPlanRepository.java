package codehows.dream.nutritionpirates.repository;

import codehows.dream.nutritionpirates.entity.Raws;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawOrderPlanRepository extends JpaRepository <Raws, Long> {

    Page<Raws> findAll(Pageable pageable);
}
