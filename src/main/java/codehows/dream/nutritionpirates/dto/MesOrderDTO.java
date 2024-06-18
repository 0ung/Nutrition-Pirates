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
	int quantity;
	boolean urgency;
	Long orderId;
	String ordererName;
	String expectedDeliveryDate;
	String product;
	Date orderDate;
}
