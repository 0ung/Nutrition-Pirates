package codehows.dream.nutritionpirates.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import codehows.dream.nutritionpirates.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
