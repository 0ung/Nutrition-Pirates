package codehows.dream.nutritionpirates.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import codehows.dream.nutritionpirates.entity.Orderer;

public interface OrdererRepository extends JpaRepository<Orderer, Long> {

	Optional<Orderer> findByName(String name);
}
