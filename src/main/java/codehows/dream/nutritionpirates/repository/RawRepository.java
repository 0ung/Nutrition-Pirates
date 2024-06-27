package codehows.dream.nutritionpirates.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import codehows.dream.nutritionpirates.constants.RawProductName;
import codehows.dream.nutritionpirates.constants.Status;
import codehows.dream.nutritionpirates.entity.Raws;

public interface RawRepository extends JpaRepository<Raws, Long> {

	Page<Raws> findAll(Pageable pageable);

	Optional<Raws> findByRawsCode(String rawsCode);

	@Query("SELECT r FROM Raws r WHERE r.status = :status AND r.deadLine BETWEEN :startDate AND :endDate")
	Page<Raws> findByStatusAndDeadlineBetween(
			@Param("status") Status status,
			@Param("startDate") Timestamp startDate,
			@Param("endDate") Timestamp endDate,
			Pageable pageable);

	List<Raws> findByProduct(RawProductName rawProductName);
}


