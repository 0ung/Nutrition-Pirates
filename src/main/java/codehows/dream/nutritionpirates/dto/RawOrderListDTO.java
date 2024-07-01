package codehows.dream.nutritionpirates.dto;

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
public class RawOrderListDTO {

    private String rawsCode;
    private String product;
    private int quantity;
    private String status;
    private String orderDate;
    private String importDate;
    private boolean isStored; // "yyyy-MM-dd HH:mm" 형식의 날짜

}
