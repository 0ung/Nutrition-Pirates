package codehows.dream.nutritionpirates.controller;


import codehows.dream.nutritionpirates.dto.ShipmentListDTO;
import codehows.dream.nutritionpirates.dto.StockShowDTO;
import codehows.dream.nutritionpirates.service.StockService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String getShipmentPage(@PathVariable("page") int page, Model model) {
        try {
            List<ShipmentListDTO> shipments = stockService.getShip(PageRequest.of(page, 10)); // Adjust page size as needed
            model.addAttribute("shipments", shipments);
            model.addAttribute("totalPages",stockService.getTotalPages());
            model.addAttribute("currentPage" , page);

            return "ChulHa"; // Corresponds to shipment.html
        } catch (Exception e) {
            log.error("Error fetching shipment data: " + e.getMessage());
            return "error"; // Handle error page
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


        // 출고 현황을 엑셀로 다운로드하는 메서드
        @GetMapping("/shipHistory")
        public ResponseEntity<?> getExcelShip(HttpServletResponse response) {
            try {
                Workbook workbook = stockService.getHistroyship();  // 엑셀 데이터 생성
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

                String fileName = "출하 현황 내역.xlsx";
                String encodeFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");

                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodeFileName);

                workbook.write(response.getOutputStream());  // 엑셀 파일을 HTTP 응답으로 전송
                workbook.close();
                return new ResponseEntity<>(HttpStatus.OK);  // 처리 성공시 OK 응답 반환
            } catch (Exception e) {
                log.error(e.getMessage());  // 로그에 에러 메시지 기록
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // 예외 발생 시 BAD_REQUEST 반환
            }
        }
}
