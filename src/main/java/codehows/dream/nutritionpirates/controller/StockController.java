package codehows.dream.nutritionpirates.controller;

import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import codehows.dream.nutritionpirates.dto.StockShowDTO;
import codehows.dream.nutritionpirates.service.StockService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller  // 스프링에게 이 클래스가 컨트롤러임을 알립니다.
@RequestMapping("/stock")  // 이 컨트롤러의 모든 매핑의 기본 경로(prefix)를 지정합니다.
@RequiredArgsConstructor  // 생성자 주입을 위한 롬복 어노테이션입니다.
@Log4j2  // Log4j2 로깅을 위한 롬복 어노테이션입니다.
public class StockController {

    private final StockService stockService;  // StockService 의존성 주입을 위한 필드

    /*@GetMapping("/{page}")
    public ResponseEntity<Page<StockShowDTO>> getStocks(Pageable pageable) {
        Page<StockShowDTO> stockShowDTOList = stockService.getStock(pageable);
        return ResponseEntity.ok(stockShowDTOList);  // 조회된 데이터와 HTTP 상태 코드를 응답으로 반환
    }*/

    // 페이지네이션을 적용하여 재고 목록을 조회하는 메서드
    @GetMapping("/list/{page}")
    public String getStocks(
            @PathVariable(name = "page") Optional<Integer> page,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        int currentPage = page.orElse(0);

        pageable = PageRequest.of(currentPage, 10, Sort.by( "id").descending());

        try {
            Page<StockShowDTO> rawStockPage = stockService.getStock(pageable);

            // 모델에 데이터 담기
            model.addAttribute("list", rawStockPage.getContent());
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("totalPages", rawStockPage.getTotalPages());

            return "stockCheck";


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "error";
        }
    }

    // 특정 재고를 출고하는 메서드
    @PutMapping("/{id}")
    public ResponseEntity<?> exportStock(@PathVariable(name = "id") Long id) {
        stockService.releaseStock(id);
        return new ResponseEntity<>(HttpStatus.OK);  // 처리 성공시 OK 응답 반환
    }

    // 재고 입고 현황 그래프를 조회하는 메서드
    @GetMapping("/graphimport")
    public ResponseEntity<?> getRawStockGraphimport() {
        try {
            return new ResponseEntity<>(stockService.getGraphStock(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // 예외 발생 시 BAD_REQUEST 반환
        }
    }

    // 재고 출고 현황 그래프를 조회하는 메서드
    @GetMapping("/graphexport")
    public ResponseEntity<?> getRawStockGraphexport() {
        try {
            return new ResponseEntity<>(stockService.getGraphStockexport(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // 예외 발생 시 BAD_REQUEST 반환
        }
    }

    // 출하 현황을 페이지네이션을 적용하여 조회하는 메서드
    // 재고 조회 
    @GetMapping("/shipment/{page}")
    public ResponseEntity<?> getship(Pageable pageable) {
        try {
            return new ResponseEntity<>(stockService.getShip(pageable), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // 예외 발생 시 BAD_REQUEST 반환
        }
    }

    // 재고 현황을 엑셀로 다운로드하는 메서드
   @GetMapping("/history")
    public ResponseEntity<?> getExcel(HttpServletResponse response) {
        try {
            Workbook workbook = stockService.getHistorystock();  // 엑셀 데이터 생성
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            String fileName = "재고 현황 내역.xlsx";
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
