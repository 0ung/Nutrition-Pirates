package codehows.dream.nutritionpirates.repository;

import codehows.dream.nutritionpirates.entity.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository <Stock, Long> {
    Page<Stock> findAll(Pageable pageable);
}
