package codehows.dream.nutritionpirates.dto;

import java.sql.Date;

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
public class MesOrderDTO {
	private int quantity;
	private boolean urgency;
	private Long orderId;
	private String ordererName;
	private String expectedDeliveryDate;
	private String product;
	private Date orderDate;
}
