package codehows.dream.nutritionpirates.service;


import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.dto.StockShowDTO;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.repository.StockRepository;
import codehows.dream.nutritionpirates.repository.WorkPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class StockService {

    private final StockRepository stockRepository;
    private final WorkPlanService workPlanService;
    private final WorkPlanRepository workPlanRepository;


    /*public StockShowDTO getStockByWorkPlan(Long id) {
        WorkPlan workPlan = workPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("WorkPlan not found for id: " + id));

        if (workPlan.getFacility() == Facility.boxMachine) {
            return StockShowDTO.builder()

                    .lotCode(workPlan.getLotCode().toString())
                    .quantity(workPlan.getSemiProduct()*0.97)
                    //.createDate(workPlan.getEndTime())
                    .build();
        } else {
            throw new IllegalArgumentException("Facility is not boxMachine for WorkPlan id: " + id);
        }
    }*/

    private String parseRawsCodes(String rawsCodes) {
        StringBuilder parsedCodes = new StringBuilder();
        if (rawsCodes != null) {
            for (char code : rawsCodes.toCharArray()) {
                switch (code) {
                    case 'G':
                        parsedCodes.append("흑마늘, ");
                        break;
                    case 'C':
                        parsedCodes.append("양배추, ");
                        break;
                    case 'S':
                        parsedCodes.append("석류, ");
                        break;
                    case 'P':
                        parsedCodes.append("매실, ");
                        break;
                    default:
                        break;
                }
            }
            // 마지막 쉼표와 공백 제거
            if (parsedCodes.length() > 0) {
                parsedCodes.setLength(parsedCodes.length() - 2);
            }
        }
        return parsedCodes.toString();
    }
    public List<StockShowDTO> getStock(Pageable pageable) {
        Page<WorkPlan> workPlanPage = workPlanRepository.findAll(pageable);
        List<StockShowDTO> stockShowDTOList = new ArrayList<>();

        for (WorkPlan workPlan : workPlanPage) {
            if (workPlan.getFacility() == Facility.boxMachine) {
                StockShowDTO dto = StockShowDTO.builder()
                        .product(parseRawsCodes(workPlan.getRawsCodes()))
                        .lotCode(workPlan.getLotCode() != null ? workPlan.getLotCode().getLotCode() : null)
                        .quantity(workPlan.getSemiProduct() * 0.97)
                        .createDate(new Date(workPlan.getEndTime().getTime()))
                        .build();
                stockShowDTOList.add(dto);
            }
        }
        return stockShowDTOList;
    }

}
