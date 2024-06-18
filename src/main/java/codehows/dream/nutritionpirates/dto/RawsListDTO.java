package codehows.dream.nutritionpirates.dto;


import lombok.*;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RawsListDTO {

    private String rawsCode;
    private String product;
    private String status;
    private Date Date;
    private int quantity;
    private String rawsReason;

}
