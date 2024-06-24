package codehows.dream.nutritionpirates.dto;


import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RawBOMDTO {

    /*private double ingredient1;
    private double ingredient2;
    private double paper;
    private double box;

    public RawBOMDTO(double ingredient1, double ingredient2, double paper, double box) {
        this.ingredient1 = ingredient1;
        this.ingredient2 = ingredient2;
        this.paper = paper;
        this.box = box;
    }*/

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
