package codehows.dream.nutritionpirates.repository;

import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;

import codehows.dream.nutritionpirates.entity.ProgramTime;

public interface ProgramTimeRepository extends JpaRepository<ProgramTime, Date> {

}
