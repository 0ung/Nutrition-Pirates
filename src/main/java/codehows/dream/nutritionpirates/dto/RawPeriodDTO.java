package codehows.dream.nutritionpirates.dto;


import lombok.*;
import java.sql.Date;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RawPeriodDTO {

    private String rawsCode;
    private String product;
    private Date importDate;
    private Date deadLine;
    private int quantity;

}
