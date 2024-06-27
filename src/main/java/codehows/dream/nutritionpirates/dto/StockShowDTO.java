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
public class StockShowDTO {

    private String product;
    private String lotCode;
    private double quantity;
    private Date createDate;
    private Date exportDate;
    private boolean isExport;

}
