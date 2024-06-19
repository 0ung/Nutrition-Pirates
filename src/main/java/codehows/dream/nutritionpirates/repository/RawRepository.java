package codehows.dream.nutritionpirates.repository;

import codehows.dream.nutritionpirates.constants.Status;
import codehows.dream.nutritionpirates.entity.Raws;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.Optional;

public interface RawRepository extends JpaRepository<Raws, Long> {

    Page<Raws> findAll(Pageable pageable);
    Optional<Raws> findByRawsCode (String rawsCode);

    @Query("SELECT r FROM Raws r WHERE r.status = :status AND r.deadLine BETWEEN :startDate AND :endDate")
    Page<Raws> findByStatusAndDeadlineBetween(
            @Param("status") Status status,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable);
}


