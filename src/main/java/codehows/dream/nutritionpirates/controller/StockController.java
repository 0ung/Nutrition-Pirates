package codehows.dream.nutritionpirates.controller;


import codehows.dream.nutritionpirates.dto.StockShowDTO;
import codehows.dream.nutritionpirates.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class StockController {

    private final StockService stockService;

    @GetMapping("/stock/{page}")
    public ResponseEntity<List<StockShowDTO>> getStocks(Pageable pageable) {
        List<StockShowDTO> stockShowDTOList = stockService.getStock(pageable);
        return ResponseEntity.ok(stockShowDTOList);
    }
}
