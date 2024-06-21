package codehows.dream.nutritionpirates.dto;


import codehows.dream.nutritionpirates.constants.ProductName;
import codehows.dream.nutritionpirates.constants.RawProductName;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RawOrderPlanDTO {

    private String partner;
    private RawProductName product;
    private double quantity;
    private Timestamp expectedImportDate;
}


