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

    /*private String partner;
    private RawProductName product;
    private double quantity;
    private Timestamp expectedImportDate;*/

    private String partnerName;
    private String product; // 한글 제품명
    private double remainingQuantity;
    private String expectedImportDate; // "yyyy-MM-dd HH:mm" 형식의 날짜
}


