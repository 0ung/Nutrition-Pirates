package codehows.dream.nutritionpirates.dto;


import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RawBOMDTO {

    private double ingredient1;
    private double ingredient2;
    private double paper;
    private double box;

    public RawBOMDTO(double ingredient1, double ingredient2, double paper, double box) {

        this.ingredient1 = ingredient1;
        this.ingredient2 = ingredient2;
        this.paper = paper;
        this.box = box;
    }
}
