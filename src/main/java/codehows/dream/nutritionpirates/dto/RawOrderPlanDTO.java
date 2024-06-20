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
    private ProductName product;
    private int quantity;
    //private Date expectedImportDate;
    private RawBOMDTO rawBOM;
    private Timestamp expectedImportDate;
}


