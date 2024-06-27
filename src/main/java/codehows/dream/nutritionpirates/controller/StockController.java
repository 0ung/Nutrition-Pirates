package codehows.dream.nutritionpirates.controller;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import codehows.dream.nutritionpirates.dto.StockShowDTO;
import codehows.dream.nutritionpirates.service.StockService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

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

    /*재고 입고 현황 조회  그래프 연결*/
    @GetMapping("/graphimport")
    public ResponseEntity<?> getRawStockGraphimport() {

        try {
            return new ResponseEntity<>(stockService.getGraphStock(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /*재고 출고 현황 조회 그래프 연결*/
    @GetMapping("/graphexport")
    public ResponseEntity<?> getRawStockGraphexport() {

        try {
            return new ResponseEntity<>(stockService.getGraphStockexport(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    /* 출하 현황 조회 */
    @GetMapping("/shipment/{page}")
    public ResponseEntity<?> getship(Pageable pageable) {
        try {
            return new ResponseEntity<>(stockService.getShip(pageable),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    /*재고 현황 엑셀 다운로드*/
    @GetMapping("/history")
    public ResponseEntity<?> getExcel(HttpServletResponse response) {
        try {
            Workbook workbook = stockService.getHistorystock();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            String fileName = "재고 현황 내역.xlsx";
            String encodeFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");

            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodeFileName);

            workbook.write(response.getOutputStream());
            workbook.close();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
