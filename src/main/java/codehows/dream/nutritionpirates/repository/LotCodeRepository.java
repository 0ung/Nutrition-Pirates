package codehows.dream.nutritionpirates.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import codehows.dream.nutritionpirates.entity.LotCode;

public interface LotCodeRepository extends JpaRepository<LotCode, String> {
	LotCode findByPervLot(String perLotcode);

	@Query(value = "SELECT * FROM lot_code WHERE lot_code_id LIKE %:param1% OR lot_code_id LIKE %:param2%", nativeQuery = true)
	Page<LotCode> findByLotCodeContaining(@Param("param1") String param1, @Param("param2") String param2, Pageable pageable);
}
