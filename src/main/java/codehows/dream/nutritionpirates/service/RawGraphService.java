package codehows.dream.nutritionpirates.service;

import codehows.dream.nutritionpirates.constants.RawProductName;
import codehows.dream.nutritionpirates.dto.RawShowGraphDTO;
import codehows.dream.nutritionpirates.entity.Raws;
import codehows.dream.nutritionpirates.repository.RawStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class RawGraphService {

    private final RawStockRepository rawStockRepository;
    public List<RawShowGraphDTO> getRawStockGraph() {

        //List<RawsShowGraphDTO> list = new ArrayList<>();

        Map<String, Integer> productQuantityMap = new HashMap<>();
        for(RawProductName productName : RawProductName.values()) {
            productQuantityMap.put(productName.getValue(), 0); // 기본값은 0으로 설정
        }

        List<Raws> rawsList = rawStockRepository.findAll();

        for (Raws raws : rawsList) {
            String product = raws.getProduct().getValue();
            int quantity = raws.getQuantity();
            productQuantityMap.put(product, productQuantityMap.get(product) + quantity);
        }

        List<RawShowGraphDTO> list = new ArrayList<>();
        productQuantityMap.forEach((product, quantity) -> {
            list.add(new RawShowGraphDTO(product,quantity));
        });

        /* ProductName 관련 Product 값을 기본 값 0이라도 설정해주기
        for(RawProductName productName : RawProductName.values()) {
            list.add(RawsShowGraphDTO.builder()
                    .product(productName.getValue())
                    .quantity(0)
                    .build());
        }

        List<Raws> rawsList = rawGraphRepository.findAll();

        Map<String, RawsShowGraphDTO> map = new HashMap<>();
        for (RawsShowGraphDTO dto : list) {
            map.put(dto.getProduct(), dto);
        }

        for (Raws raws : rawsList) {
            RawsShowGraphDTO dto = map.get(raws.getProduct().getValue());
            dto.setQuantity(raws.getQuantity());
        }

        return new ArrayList<>(map.values());*/
        return list;
    }

    public List<String> checkMinimumStock() {
        List<String> notification = new ArrayList<>();

        List<Raws> rawsList = rawStockRepository.findAll();

        Map<RawProductName, Integer> stockMap = new HashMap<>();
        for(RawProductName productName : RawProductName.values()) {
            stockMap.put(productName, 0); // 수량없을때 초기값 0설정
        }

        for(Raws raws : rawsList) {
            RawProductName product = raws.getProduct();
            int quantity = raws.getQuantity();
            stockMap.put(product, stockMap.get(product) + quantity);
        }

        stockMap.forEach((product, quantity) -> {
            switch (product) {
                case CABBAGE,BLACK_GARLIC:
                    if(quantity <= 1000) {
                        notification.add(product.getValue() + " 의 재고가 1000kg 이하입니다. 현재 수량은 " + quantity + " kg");
                    }
                    break;
                case POMEGRANATE,PLUM,HONEY,COLLAGEN:
                    if(quantity <= 100) {
                        notification.add(product.getValue() + " 의 재고가 100L 이하입니다. 현재 수량은 " + quantity + " L");
                    }
                    break;
                case WRAPPING_PAPER:
                    if(quantity <= 50000) {
                        notification.add(product.getValue() + " 의 재고가 50000개 이하입니다. 현재 수량은 " + quantity + " 개");
                    }
                    break;
                case BOX:
                    if(quantity <= 5000) {
                        notification.add(product.getValue() + " 의 재고가 5000개 이하입니다. 현재 수량은 "+ quantity + " 개");
                    }
                    break;
                default:
                    break;
            }
        });

        return notification;
    }
}
