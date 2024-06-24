package codehows.dream.nutritionpirates.dto;

import lombok.*;

import java.sql.Date;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class StockShowDTO {

    private Long id;
    private String LotCode;
    private int quantity;
    private Date createDate;
    private Date exportDate;
    private boolean isExport;
}
