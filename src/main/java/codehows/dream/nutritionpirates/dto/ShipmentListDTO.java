package codehows.dream.nutritionpirates.dto;

import java.sql.Date;

import codehows.dream.nutritionpirates.constants.Process;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ShipmentListDTO {

    private Long id;
    private String orderName;
    private String product;
    private int quantity;
    private Date orderDate;
    private Date exportDate;
    private String expectedDeliveryDate;
    private Process process;
    private boolean urgency;
    private boolean isShipping;

}
