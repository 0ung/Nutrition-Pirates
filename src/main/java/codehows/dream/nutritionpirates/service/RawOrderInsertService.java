package codehows.dream.nutritionpirates.service;


import codehows.dream.nutritionpirates.constants.RawProductName;
import codehows.dream.nutritionpirates.dto.RawOrderInsertDTO;
import codehows.dream.nutritionpirates.dto.RawOrderPlanDTO;
import codehows.dream.nutritionpirates.entity.Raws;
import codehows.dream.nutritionpirates.repository.RawOrderInsertRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor

public class RawOrderInsertService {

    private final RawOrderInsertRepository rawOrderInsertRepository;
    public void insert(RawOrderInsertDTO rawOrderInsertDTO) {

        Date date = Date.valueOf(LocalDate.now());

        String rawsCode = createRawsCodes(rawOrderInsertDTO);

        Raws raws = Raws.builder()
                .product(rawOrderInsertDTO.getProduct())
                .rawsCode(rawsCode)
                .quantity(rawOrderInsertDTO.getQuantity())
                .partner(rawOrderInsertDTO.getPartner())
                .orderDate(date)
                .build();

        rawOrderInsertRepository.save(raws);
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



}
