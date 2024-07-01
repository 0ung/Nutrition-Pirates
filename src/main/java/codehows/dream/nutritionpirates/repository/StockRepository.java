package codehows.dream.nutritionpirates.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import codehows.dream.nutritionpirates.entity.Stock;

public interface StockRepository extends JpaRepository <Stock, Long> {
    Page<Stock> findAll(Pageable pageable);

    List<Stock> findByExportDateIsNull();


}
