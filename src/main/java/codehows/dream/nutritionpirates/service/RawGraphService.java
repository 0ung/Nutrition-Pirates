package codehows.dream.nutritionpirates.service;

import codehows.dream.nutritionpirates.constants.RawProductName;
import codehows.dream.nutritionpirates.constants.Status;
import codehows.dream.nutritionpirates.dto.RawPeriodDTO;
import codehows.dream.nutritionpirates.dto.RawShowGraphDTO;
import codehows.dream.nutritionpirates.entity.Raws;
import codehows.dream.nutritionpirates.repository.RawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class RawGraphService {

    private final RawRepository rawRepository;

    // 입고된 양만 list에 담아서 보여주기
    public List<RawShowGraphDTO> getRawStockGraph() {

        Map<String, Integer> productQuantityMap = new HashMap<>();
        for(RawProductName productName : RawProductName.values()) {
            productQuantityMap.put(productName.getValue(), 0); // 기본값은 0으로 설정
        }
        List<Raws> rawsList = rawRepository.findAll();

        for (Raws raws : rawsList) {
            if (raws.getStatus() == Status.IMPORT) {
                String product = raws.getProduct().getValue();
                int quantity = raws.getQuantity();
                productQuantityMap.put(product, productQuantityMap.get(product) + quantity);
            }
        }

        List<RawShowGraphDTO> list = new ArrayList<>();
        productQuantityMap.forEach((product, quantity) -> {
            list.add(new RawShowGraphDTO(product,quantity));
        });

        return list;
    }

    // 최소 원자재 보유량 알림
    public List<String> checkMinimumStock() {
        List<String> notification = new ArrayList<>();

        List<Raws> rawsList = rawRepository.findAll();

        Map<RawProductName, Integer> stockMap = new HashMap<>();
        for(RawProductName productName : RawProductName.values()) {
            stockMap.put(productName, 0); // 수량없을때 초기값 0설정
        }

        for(Raws raws : rawsList) {
            if (raws.getStatus() == Status.IMPORT) {
                RawProductName product = raws.getProduct();
                int quantity = raws.getQuantity();
                stockMap.put(product, stockMap.get(product) + quantity);
            }
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


    public List<RawPeriodDTO> getPeriodList(Pageable pageable) {

        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.minusDays(2);  // 3일 전부터
        LocalDate endDate = currentDate;  // 오늘까지

        Page<Raws> pages = rawRepository.findByStatusAndDeadlineBetween(
                Status.IMPORT,
                java.sql.Date.valueOf(startDate),
                java.sql.Date.valueOf(endDate),
                pageable);

        List<RawPeriodDTO> list = new ArrayList<>();

        pages.forEach(e -> list.add(RawPeriodDTO.builder()
                .rawsCode(e.getRawsCode())
                .product(e.getProduct().getValue())
                .importDate(e.getImportDate())
                .deadLine(e.getDeadLine())
                .quantity(e.getQuantity())
                .build()));

        return list;
    }
    /*public List<RawPeriodDTO> getPeriodList(Pageable pageable) {

        LocalDate currentDate = LocalDate.now();
        LocalDate minDate = currentDate.minus(ChronoUnit.DAYS.between(currentDate, currentDate.minusDays(3)), ChronoUnit.DAYS);
        LocalDate maxDate = currentDate.plusDays(3);

        Page<Raws> pages = rawRepository.findByStatusAndDeadlineBetween(
                Status.IMPORT,
                java.sql.Date.valueOf(minDate),
                java.sql.Date.valueOf(maxDate),
                pageable);

        List<RawPeriodDTO> list = new ArrayList<>();

        //Page<Raws> pages = rawRepository.findAll(pageable);

        //LocalDate currentDate = LocalDate.now();

        pages.forEach((e) -> {
            /*if (e.getStatus() == Status.IMPORT && e.getDeadLine() != null) {
                LocalDate deadlineDate = e.getDeadLine().toLocalDate();
                long daysUntilDeadline = ChronoUnit.DAYS.between(deadlineDate, currentDate);

                    if ( daysUntilDeadline <=3) {
                        list.add(RawPeriodDTO.builder()
                                .rawsCode(e.getRawsCode())
                                .product(e.getProduct().getValue())
                                .importDate(e.getImportDate())
                                .deadLine(e.getDeadLine())
                                .quantity(e.getQuantity())
                                .build());
        });
        return list;
    } */
}
