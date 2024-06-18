package codehows.dream.nutritionpirates.service;


import codehows.dream.nutritionpirates.dto.StockShowDTO;
import codehows.dream.nutritionpirates.entity.Stock;
import codehows.dream.nutritionpirates.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class StockService {

    private final StockRepository stockRepository;

    public List<StockShowDTO> getStockList(Pageable pageable) {
        List<StockShowDTO> list = new ArrayList<>();
        Page<Stock> pages = stockRepository.findAll(pageable);

        pages.forEach((e) -> {
            Stock stock = stockRepository.findById(e.getId()).orElse(null);
            list.add(StockShowDTO.builder()
                            .id(e.getId())
                            .quantity(e.getQuantity())
                            .createDate(e.getCreateDate())
                            .exportDate(e.getExportDate())
                            .isExport(e.isExport())
                            .build());

        });

        return list;
    }
}
