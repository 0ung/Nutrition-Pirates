package codehows.dream.nutritionpirates.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LotCode {

	@Id
	@Column(name = "lotCode")
	private String letCode;

	private String pervLot;

	public LotCode(String letCode) {
		this.letCode = letCode;
	}
}
