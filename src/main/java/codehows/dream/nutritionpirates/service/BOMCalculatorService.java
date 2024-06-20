package codehows.dream.nutritionpirates.service;

import codehows.dream.nutritionpirates.constants.ProductName;
import codehows.dream.nutritionpirates.constants.RawProductName;
import codehows.dream.nutritionpirates.dto.RawBOMDTO;
import codehows.dream.nutritionpirates.dto.RawOrderPlanDTO;
import codehows.dream.nutritionpirates.entity.Order;
import codehows.dream.nutritionpirates.entity.Raws;
import codehows.dream.nutritionpirates.repository.OrderRepository;
import codehows.dream.nutritionpirates.repository.RawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BOMCalculatorService {

    private final OrderRepository orderRepository;
    private final RawRepository rawRepository;
    //첫번쨰 생성이 되면
    //분류
    public RawBOMDTO createRequirement(Order order){
        ProductName productName = order.getProduct();
        switch (productName){
			case BLACK_GARLIC_JUICE -> {
				return garlicJuiceBOMCal(order.getQuantity());
			}
			case CABBAGE_JUICE -> {
                return cabbageJuiceBOMCal(order.getQuantity());
            }
            case POMEGRANATE_JELLY_STICK -> {
                return pomegranateStickBOMCal(order.getQuantity());
            }
            case PLUM_JELLY_STICK -> {
                return plumStickBOMCal(order.getQuantity());
            }
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

        //return new RawBOMDTO(garlic, honey, paper, box);
        return new RawBOMDTO(0, garlic, 0, 0, honey, 0, paper, box);
    }

    public RawBOMDTO cabbageJuiceBOMCal(int quantity) {
        double quantity1 = Math.ceil(Math.ceil(Math.ceil(quantity * 30 / 0.97) * 133.33) / 1000);
        double cabbage = Math.ceil(quantity1);

        double quantity2 = Math.ceil(quantity * 30 / 0.97) * 5 / 1000;
        double honey = Math.ceil(quantity2);

        double paper = Math.ceil(quantity * 30 / 0.97);
        double box = Math.ceil(quantity / 0.97);

        //return new RawBOMDTO(cabbage, honey, paper, box);
        return new RawBOMDTO(cabbage, 0, 0, 0, honey, 0, paper, box);
    }

    public RawBOMDTO pomegranateStickBOMCal(int quantity) {
        double quantity1 = Math.ceil(Math.ceil(Math.ceil(quantity * 25 / 0.97) * 5) / 1000);
        double pomegranate = Math.ceil(quantity1);

        double quantity2 = Math.ceil(quantity * 25 / 0.97) * 2 / 1000;
        double collagen = Math.ceil(quantity2);

        double paper = Math.ceil(quantity * 25 / 0.97);
        double box = Math.ceil(quantity / 0.97);

        //return new RawBOMDTO(pomegranate, collagen, paper, box);
        return new RawBOMDTO(0, 0, pomegranate, 0, 0, collagen, paper, box);
    }

    public RawBOMDTO plumStickBOMCal(int quantity) {
        double quantity1 = Math.ceil(Math.ceil(Math.ceil(quantity * 25 / 0.97) * 5) / 1000);
        double plum = Math.ceil(quantity1);

        double quantity2 = Math.ceil(quantity * 25 / 0.97) * 2 / 1000;
        double collagen = Math.ceil(quantity2);

        double paper = Math.ceil(quantity * 25 / 0.97);
        double box = Math.ceil(quantity / 0.97);

        //return new RawBOMDTO(plum, collagen, paper, box);
        return new RawBOMDTO(0, 0, 0, plum, 0, collagen, paper, box);
    }


    // 각 주문에 대한 BOM 계산 및 결과 리스트 반환
    public List<RawBOMDTO> calculateBOMs() {
        List<Order> orders = orderRepository.findAll();
        List<RawBOMDTO> result = new ArrayList<>();

        for (Order order : orders) {
            RawBOMDTO bomDTO = createRequirement(order);
            result.add(bomDTO);
        }
        return result;
    }

    // aggregateQuantities 메서드 추가
    public List<RawBOMDTO> aggregateQuantities(List<RawBOMDTO> bomDTOList) {
        List<RawBOMDTO> aggregatedList = new ArrayList<>();

        // 초기화
        RawBOMDTO aggregateDTO = new RawBOMDTO();

        // 각 RawBOMDTO 객체에서 값을 추출하여 더하기
        for (RawBOMDTO bomDTO : bomDTOList) {
            aggregateDTO.setCabbage(aggregateDTO.getCabbage() + bomDTO.getCabbage());
            aggregateDTO.setGarlic(aggregateDTO.getGarlic() + bomDTO.getGarlic());
            aggregateDTO.setPomegranate(aggregateDTO.getPomegranate() + bomDTO.getPomegranate());
            aggregateDTO.setPlum(aggregateDTO.getPlum() + bomDTO.getPlum());
            aggregateDTO.setHoney(aggregateDTO.getHoney() + bomDTO.getHoney());
            aggregateDTO.setCollagen(aggregateDTO.getCollagen() + bomDTO.getCollagen());
            aggregateDTO.setPaper(aggregateDTO.getPaper() + bomDTO.getPaper());
            aggregateDTO.setBox(aggregateDTO.getBox() + bomDTO.getBox());
        }

        // 합산된 결과를 리스트에 추가
        aggregatedList.add(aggregateDTO);

        return aggregatedList;
    }

}

