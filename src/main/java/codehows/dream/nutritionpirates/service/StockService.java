package codehows.dream.nutritionpirates.service;


import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.dto.StockShowDTO;
import codehows.dream.nutritionpirates.entity.Stock;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.repository.StockRepository;
import codehows.dream.nutritionpirates.repository.WorkPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class StockService {

    private final StockRepository stockRepository;
    private final WorkPlanService workPlanService;
    private final WorkPlanRepository workPlanRepository;
    private final ProgramTimeService programTimeService;


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
        if (rawsCodes == null) {
            return null;
        }

        if (rawsCodes.contains("G")) {
            return "흑마늘";
        } else if (rawsCodes.contains("C")) {
            return "양배추";
        } else if (rawsCodes.contains("S")) {
            return "석류";
        } else if (rawsCodes.contains("P")) {
            return "매실";
        }

        return "알 수 없음"; // 정의되지 않은 경우
    }

    public List<StockShowDTO> getStock(Pageable pageable) {
        Page<WorkPlan> workPlanPage = workPlanRepository.findAll(pageable);
        List<StockShowDTO> stockShowDTOList = new ArrayList<>();

        for (WorkPlan workPlan : workPlanPage) {
            String product = null;

            if (workPlan.getFacility() == Facility.washer) {
                //rawCodes 값으로 상품명 값을 반환
                product = parseRawsCodes(workPlan.getRawsCodes());
            }

            if (workPlan.getFacility() == Facility.boxMachine) {

                StockShowDTO dto = StockShowDTO.builder()
                        .product(product)
                        .lotCode(workPlan.getLotCode() != null ? workPlan.getLotCode().getLotCode() : null)
                        .quantity(workPlan.getSemiProduct() * 0.97)
                        .createDate(new Date(workPlan.getEndTime().getTime()))

                        .build();
                stockShowDTOList.add(dto);
            }

        }
        return stockShowDTOList;
    }

    public void releaseStock (Long id) {
        Stock stock = stockRepository.findById(id).orElse(null);

        // 프로그램 시간설정 적용
        Timestamp timestamp = programTimeService.getProgramTime().getCurrentProgramTime();
        Date exportDate = new Date(timestamp.getTime());

        stock.setExportDate(exportDate);
        stock.setExport(true);
        stockRepository.save(stock);
    }
}
