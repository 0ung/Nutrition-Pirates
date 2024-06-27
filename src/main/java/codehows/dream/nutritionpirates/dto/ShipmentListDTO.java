package codehows.dream.nutritionpirates.dto;

import codehows.dream.nutritionpirates.constants.Process;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ShipmentListDTO {

    private String orderName;
    private String product;
    private int quantity;
    private Date orderDate;
    private Date shippingDate;
    private String expectedDeliveryDate;
    private Process process;
    private boolean urgency;
    private boolean isShipping;

}
