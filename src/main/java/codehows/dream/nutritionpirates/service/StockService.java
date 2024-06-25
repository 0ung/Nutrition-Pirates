package codehows.dream.nutritionpirates.service;


import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.constants.ProductName;
import codehows.dream.nutritionpirates.dto.StockGraphDTO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    // isExport 가 1(true) 이면 입고 0 (false) 이면 출고
    public List<StockShowDTO> getStock(Pageable pageable){
        Page<Stock> stocks = stockRepository.findAll(pageable);
        List<StockShowDTO> stockShowDTOList = new ArrayList<>();

        stocks.forEach((e)->{
            stockShowDTOList.add(
                    StockShowDTO.builder()
                            .product(e.getWorkPlan().getProcessPlan().getOrder().getProduct().getValue())
                            .lotCode(e.getWorkPlan().getLotCode().getLotCode())
                            .quantity(e.getQuantity())
                            .createDate(e.getCreateDate())
                            .isExport(e.getExportDate() == null? false : true )
                            .exportDate(e.getExportDate())
                            .build()
            );
        });
        return stockShowDTOList;
    }

    public void releaseStock (Long id) {
        Stock stock = stockRepository.findById(id).orElse(null);

        // 프로그램 시간설정 적용
        Timestamp timestamp = programTimeService.getProgramTime().getCurrentProgramTime();
        Date exportDate = new Date(timestamp.getTime());

        stock.updateIsExport(false, exportDate);
        stockRepository.save(stock);
    }

    public List<StockGraphDTO> getGraphStock () {

        Map<String, Integer> stockQuantityMap = new HashMap<>();
        for (ProductName productName : ProductName.values()) {
            stockQuantityMap.put(productName.getValue(), 0); // 기본값으로 설정
        }

        List<Stock> stockList = stockRepository.findAll();

        for(Stock stock : stockList) {
            if(stock.isExport() == false) {
                String product = ProductName.CABBAGE_JUICE.getValue();
                int quantity = stock.getQuantity();
                stockQuantityMap.put(product, stockQuantityMap.get(product) + quantity);
            }
        }
        List<StockGraphDTO> list = new ArrayList<>();
        stockQuantityMap.forEach((product, quantity) -> {
            list.add(new StockGraphDTO(product, quantity));
        });
        return list;
    }

}
