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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/stock")
@RequiredArgsConstructor
@Log4j2
public class StockController {

    private final StockService stockService;

    @GetMapping("/{page}")
    public ResponseEntity<List<StockShowDTO>> getStocks(Pageable pageable) {
        List<StockShowDTO> stockShowDTOList = stockService.getStock(pageable);
        return ResponseEntity.ok(stockShowDTOList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> exportStock(@PathVariable(name = "id") Long id) {
        stockService.releaseStock(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*원자재 현황 조회 그래프 연결*/
    @GetMapping("/graph")
    public ResponseEntity<?> getRawStockGraph() {

        try {
            return new ResponseEntity<>(stockService.getGraphStock(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
