package codehows.dream.nutritionpirates.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RawBOMDTO {

    private double cabbage;
    private double garlic;
    private double pomegranate;
    private double plum;
    private double honey;
    private double collagen;
    private double paper;
    private double box;

    public RawBOMDTO(double cabbage, double garlic, double pomegranate, double plum, double honey, double collagen, double paper, double box) {
        this.cabbage = cabbage;
        this.garlic = garlic;
        this.pomegranate = pomegranate;
        this.plum = plum;
        this.honey = honey;
        this.collagen = collagen;
        this.paper = paper;
        this.box = box;
    }
}
