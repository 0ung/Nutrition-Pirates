package codehows.dream.nutritionpirates.service;


import codehows.dream.nutritionpirates.constants.RawProductName;
import codehows.dream.nutritionpirates.constants.Status;
import codehows.dream.nutritionpirates.dto.RawOrderInsertDTO;
import codehows.dream.nutritionpirates.dto.RawOrderPlanDTO;
import codehows.dream.nutritionpirates.dto.RawsListDTO;
import codehows.dream.nutritionpirates.entity.Raws;
import codehows.dream.nutritionpirates.repository.RawOrderInsertRepository;
import codehows.dream.nutritionpirates.repository.RawOrderPlanRepository;
import codehows.dream.nutritionpirates.repository.RawStockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor

public class RawOrderInsertService {

    private final RawOrderInsertRepository rawOrderInsertRepository;
    private final RawOrderPlanRepository rawOrderPlanRepository;
    private final RawStockRepository rawStockRepository;

    public void insert(RawOrderInsertDTO rawOrderInsertDTO) {

        validateQuantity(rawOrderInsertDTO);
        Date date = Date.valueOf(LocalDate.now());

        String rawsCode = createRawsCodes(rawOrderInsertDTO);
        Raws raws = Raws.builder()
                .product(rawOrderInsertDTO.getProduct())
                .rawsCode(rawsCode)
                .quantity(rawOrderInsertDTO.getQuantity())
                .partner(rawOrderInsertDTO.getPartner())
                .orderDate(date)
                .status(Status.WAITING)
                .build();

        rawOrderInsertRepository.save(raws);
    }

    public void validateQuantity(RawOrderInsertDTO rawOrderInsertDTO) {
        int quantity = rawOrderInsertDTO.getQuantity();
        RawProductName product = rawOrderInsertDTO.getProduct();

        switch (product) {
            case CABBAGE:
            case BLACK_GARLIC:
                if (quantity < 100 || quantity > 2000) {
                    throw new IllegalArgumentException("Quantity for " + product.getValue() + "100에서 2000 주문");
                }
                break;
            case POMEGRANATE:
            case PLUM:
            case COLLAGEN:
                if (quantity < 20 || quantity > 200) {
                    throw new IllegalArgumentException("Quantity for " + product.getValue() + "20에서 200 주문");
                }
                break;
            case HONEY:
                if (quantity < 1 || quantity > 200) {
                    throw new IllegalArgumentException("Quantity for " + product.getValue() + "1에서 200 주문");
                }
                break;
            case WRAPPING_PAPER:
                if (quantity < 10000 || quantity > 100000) {
                    throw new IllegalArgumentException("Quantity for " + product.getValue() + "10000에서 100000 주문");
                }
                break;
            case BOX:
                if (quantity < 1000 || quantity > 10000) {
                    throw new IllegalArgumentException("Quantity for " + product.getValue() + "1000에서 10000 주문");
                }
                break;
            default:
                throw new IllegalArgumentException("상품명 오류 " + product.getValue());
        }
    }
    public void rawImport (String rawsCode) {

        Raws raw = rawStockRepository.findByRawsCode(rawsCode).orElse(null);
        raw.rawImport();
        rawStockRepository.save(raw);
    }

    public void rawExport(String rawsCode) {
        Raws raw = rawStockRepository.findByRawsCode(rawsCode).orElse(null);
        raw.rawExport();
        rawStockRepository.save(raw);
    }

    private RawProductName getRawProductName(String Raw) {
        switch (Raw) {
            case "양배추" :
                return RawProductName.CABBAGE;
            case "흑마늘" :
                return RawProductName.BLACK_GARLIC;
            case "석류(농축액)" :
                return RawProductName.POMEGRANATE;
            case "매실(농축액)" :
                return RawProductName.PLUM;
            case "벌꿀" :
                return RawProductName.HONEY;
            case "콜라겐" :
                return RawProductName.COLLAGEN;
            case "포장지" :
                return RawProductName.WRAPPING_PAPER;
            case "박스" :
                return RawProductName.BOX;
            default:
                return null;
        }
    }

    private String createRawsCodes(RawOrderInsertDTO rawOrderInsertDTO) {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = now.format(formatter);

        int quantity = rawOrderInsertDTO.getQuantity();
        String rawCode = getRaws(rawOrderInsertDTO.getProduct());

        String rawsCode = formattedDate + quantity + rawCode;

        return rawsCode;
    }



    private String getRaws(RawProductName productName){
        String code;
        switch (productName) {
            case CABBAGE :
                 code = "C";
                 break;
            case BLACK_GARLIC :
                code = "B";
                break;
            case POMEGRANATE :
                code = "S";
                break;
            case PLUM :
                code = "P";
                break;
            case HONEY :
                code = "H";
                break;
            case COLLAGEN :
                code = "L";
                break;
            case WRAPPING_PAPER :
                code = "P";
                break;
            case BOX :
                code = "B";
                break;
            default:
                throw new IllegalArgumentException("없는 상품원자재코드 : " + productName);
        }
        return code;
    }

    public List<RawOrderPlanDTO> getRawOrderList(Pageable pageable) {

        List<RawOrderPlanDTO> list = new ArrayList<>();

        Page<Raws> pages = rawOrderPlanRepository.findAll(pageable);

        pages.forEach((e) -> {

            list.add(RawOrderPlanDTO.builder()
                    .partner(e.getPartner())
                    .product(e.getProduct().getValue())
                    .quantity(e.getQuantity())
                    .expectedImportDate(e.getExpectedImportDate())
                    .build());
        });

        return list;
    }

    public List<RawsListDTO> getRawStockList(Pageable pageable) {

        List<RawsListDTO> list = new ArrayList<>();

        Page<Raws> pages = rawStockRepository.findAll(pageable);

        pages.forEach((e) -> {

            list.add(RawsListDTO.builder()
                    .rawsCode(e.getRawsCode())
                    .status(e.getStatus().getValue())
                    .product(e.getProduct().getValue())
                    .importDate(e.getImportDate())
                    .quantity(e.getQuantity())
                    .rawsReason(e.getRawsReason()!= null ? e.getRawsReason().getValue() : "")
                    .build());
        });
        return list;
    }


}
