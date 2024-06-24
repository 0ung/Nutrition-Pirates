package codehows.dream.nutritionpirates.dto;

import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class StockShowDTO {

    private String product;
    private String lotCode;
    private double quantity;
    private Date createDate;
    private Date exportDate;
    private boolean isExport;
}
