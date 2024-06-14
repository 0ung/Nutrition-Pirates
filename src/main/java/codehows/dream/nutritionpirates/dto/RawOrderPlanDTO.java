package codehows.dream.nutritionpirates.dto;


import codehows.dream.nutritionpirates.constants.RawProductName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@NoArgsConstructor
@Getter
@Setter
public class RawOrderPlanDTO {

    private String partner;
    private RawProductName product;
    private Integer quantity;
    private Date expectedImportDate;
}


