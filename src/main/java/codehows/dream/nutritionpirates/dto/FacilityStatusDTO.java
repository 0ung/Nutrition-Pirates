package codehows.dream.nutritionpirates.dto;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.FacilityStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityStatusDTO {
	private Long workPlanId;
	private Facility facility;
	private FacilityStatus facilityStatus;
	private String process;
}
