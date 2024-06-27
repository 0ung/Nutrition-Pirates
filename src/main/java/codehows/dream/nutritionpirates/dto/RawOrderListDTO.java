package codehows.dream.nutritionpirates.dto;


import lombok.*;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RawOrderListDTO {

    private String rawsCode;
    private String product;
    private int quantity;
    private String status;
    private String orderDate;
    private String importDate;
    private boolean isStored; // "yyyy-MM-dd HH:mm" 형식의 날짜

}
