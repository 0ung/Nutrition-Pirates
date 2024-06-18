package codehows.dream.nutritionpirates.service;

import codehows.dream.nutritionpirates.constants.ProductName;
import codehows.dream.nutritionpirates.dto.RawBOMDTO;
import codehows.dream.nutritionpirates.entity.Order;
import org.springframework.stereotype.Service;


@Service
public class BOMCalculatorService {


    //첫번쨰 생성이 되면
    //분류
    public void createRequirement(Order order){
        ProductName productName = order.getProduct();
        switch (productName){
            case BLACK_GARLIC_JUICE -> garlicJuiceBOMCal(order.getQuantity());
            case CABBAGE_JUICE -> cabbageJuiceBOMCal(order.getQuantity());
            case POMEGRANATE_JELLY_STICK -> pomegranateStickBOMCal(order.getQuantity());
            case PLUM_JELLY_STICK -> plumStickBOMCal(order.getQuantity());
            //커스텀 Exception 반드시 구현
            default ->throw new RuntimeException();
        }

    }
    public RawBOMDTO garlicJuiceBOMCal(int quantity) {
        double quantity1 = Math.ceil(Math.ceil(Math.ceil(quantity * 30 / 0.97) * 133.33) / 1000);
        double garlic = Math.ceil(quantity1);

        double quantity2 = Math.ceil(quantity * 30 / 0.97) * 5 / 1000;
        double honey = Math.ceil(quantity2);

        double paper = Math.ceil(quantity * 30 / 0.97);
        double box = Math.ceil(quantity / 0.97);

        return new RawBOMDTO(garlic, honey, paper, box);
    }

    public RawBOMDTO cabbageJuiceBOMCal(int quantity) {
        double quantity1 = Math.ceil(Math.ceil(Math.ceil(quantity * 30 / 0.97) * 133.33) / 1000);
        double cabbage = Math.ceil(quantity1);

        double quantity2 = Math.ceil(quantity * 30 / 0.97) * 5 / 1000;
        double honey = Math.ceil(quantity2);

        double paper = Math.ceil(quantity * 30 / 0.97);
        double box = Math.ceil(quantity / 0.97);

        return new RawBOMDTO(cabbage, honey, paper, box);
    }

    public RawBOMDTO pomegranateStickBOMCal(int quantity) {
        double quantity1 = Math.ceil(Math.ceil(Math.ceil(quantity * 25 / 0.97) * 5) / 1000);
        double pomegranate = Math.ceil(quantity1);

        double quantity2 = Math.ceil(quantity * 25 / 0.97) * 2 / 1000;
        double collagen = Math.ceil(quantity2);

        double paper = Math.ceil(quantity * 25 / 0.97);
        double box = Math.ceil(quantity / 0.97);

        return new RawBOMDTO(pomegranate, collagen, paper, box);
    }

    public RawBOMDTO plumStickBOMCal(int quantity) {
        double quantity1 = Math.ceil(Math.ceil(Math.ceil(quantity * 25 / 0.97) * 5) / 1000);
        double plum = Math.ceil(quantity1);

        double quantity2 = Math.ceil(quantity * 25 / 0.97) * 2 / 1000;
        double collagen = Math.ceil(quantity2);

        double paper = Math.ceil(quantity * 25 / 0.97);
        double box = Math.ceil(quantity / 0.97);

        return new RawBOMDTO(plum, collagen, paper, box);
    }
}

