package codehows.dream.nutritionpirates.dto;


import codehows.dream.nutritionpirates.constants.RawProductName;
import lombok.*;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RawOrderPlanDTO {

    private String partner;
    private String product;
    private int quantity;
    private Date expectedImportDate;
}


