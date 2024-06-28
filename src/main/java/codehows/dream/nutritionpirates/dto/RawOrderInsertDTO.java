package codehows.dream.nutritionpirates.dto;

import java.sql.Date;
import java.sql.Timestamp;

import codehows.dream.nutritionpirates.constants.RawProductName;
import codehows.dream.nutritionpirates.constants.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter

public class RawOrderInsertDTO {

    private String rawsCode;
    private String partner;
    private Integer quantity;
    private RawProductName product;
    //private Date orderDate;
    private Timestamp orderDate;
    private Date importDate;
    private Status status;
}