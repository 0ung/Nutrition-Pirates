package codehows.dream.nutritionpirates.dto;

import java.sql.Date;

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
public class RawsListDTO {

    private String rawsCode;
    private String product;
    private String status;
    private Date Date;
    private int quantity;
    private String rawsReason;

}
