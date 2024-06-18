package codehows.dream.nutritionpirates.config;

import codehows.dream.nutritionpirates.constants.ProductName;
import codehows.dream.nutritionpirates.dto.RawBOMDTO;
import codehows.dream.nutritionpirates.entity.Order;

public class RequirementCalculation {

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
    private RawBOMDTO garlicJuiceBOMCal(int quantity){
        double quantity1 = Math.ceil(Math.ceil(Math.ceil(quantity * 30 / 0.97)*133.33)/1000);
        double garlic = Math.ceil(quantity1);
        System.out.println(garlic);

        double quantity2 = Math.ceil(quantity*30/0.97)*5/1000;
        double honey = Math.ceil(quantity2);
        System.out.println(honey);

        double paper = Math.ceil(quantity*30/0.97);
        double box = Math.ceil(quantity/0.97);
        System.out.println(paper);
        System.out.println(box);

        return new RawBOMDTO (garlic,honey, paper, box);
    }

    private RawBOMDTO cabbageJuiceBOMCal(int quantity){
        double quantity1 = Math.ceil(Math.ceil(Math.ceil(quantity * 30 / 0.97)*133.33)/1000);
        double cabbage = Math.ceil(quantity1);
        System.out.println(cabbage);

        double quantity2 = Math.ceil(quantity*30/0.97)*5/1000;
        double honey = Math.ceil(quantity2);
        System.out.println(honey);

        double paper = Math.ceil(quantity*30/0.97);
        double box = Math.ceil(quantity/0.97);
        System.out.println(paper);
        System.out.println(box);

        return new RawBOMDTO (cabbage,honey, paper, box);

    }

    private RawBOMDTO pomegranateStickBOMCal(int quantity){
        double quantity1 = Math.ceil(Math.ceil(Math.ceil(quantity * 25 / 0.97)*5)/1000) ;
        double pomegranate = Math.ceil(quantity1);
        System.out.println(pomegranate);

        double quantity2 = Math.ceil(quantity*25/0.97)*2/1000;
        double collagen = Math.ceil(quantity2);
        System.out.println(collagen);

        double paper = Math.ceil(quantity*25/0.97);
        double box = Math.ceil(quantity/0.97);
        System.out.println(paper);
        System.out.println(box);

        return new RawBOMDTO (pomegranate,collagen, paper, box);
    }

    private RawBOMDTO plumStickBOMCal(int quantity){
        double quantity1 = Math.ceil(Math.ceil(Math.ceil(quantity * 25 / 0.97)*5)/1000) ;
        double plum = Math.ceil(quantity1);
        System.out.println(plum);

        double quantity2 = Math.ceil(quantity*25/0.97)*2/1000;
        double collagen = Math.ceil(quantity2);
        System.out.println(collagen);

        double paper = Math.ceil(quantity*25/0.97);
        double box = Math.ceil(quantity/0.97);
        System.out.println(paper);
        System.out.println(box);

        return new RawBOMDTO (plum,collagen, paper, box);
    }


    public static void main(String[] args) {
        RequirementCalculation requirementCalculation = new RequirementCalculation();

        requirementCalculation.garlicJuiceBOMCal(100);
        requirementCalculation.cabbageJuiceBOMCal(100);
        requirementCalculation.pomegranateStickBOMCal(100);
        requirementCalculation.plumStickBOMCal(100);
    }

}

