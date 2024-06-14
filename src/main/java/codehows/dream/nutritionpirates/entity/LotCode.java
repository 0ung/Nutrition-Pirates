package codehows.dream.nutritionpirates.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class LotCode {

	@Id
	@Column(name = "lotCode")
	private String letCode;

	private String pervLot;
}
