package codehows.dream.nutritionpirates.repository;

import codehows.dream.nutritionpirates.entity.Raws;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RawStockRepository extends JpaRepository <Raws, Long> {

    Page<Raws> findAll(Pageable pageable);

    Optional<Raws> findByRawsCode (String rawsCode);
}
