package codehows.dream.nutritionpirates.dto;

import codehows.dream.nutritionpirates.constants.ProductName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MesOrderInsertDTO {
	private String orderName;
	private String orderNumber;
	private boolean individual;
	private int quantity;
	private boolean urgency;
	private ProductName product;
}
