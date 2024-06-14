package codehows.dream.nutritionpirates.entity;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "program_time")
public class ProgramTime {
	@Id
	private Date currentProgramTime;
}
