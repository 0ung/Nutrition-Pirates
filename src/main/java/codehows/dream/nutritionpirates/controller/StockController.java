package codehows.dream.nutritionpirates.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import codehows.dream.nutritionpirates.dto.ShipmentListDTO;
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

	private final StockService stockService;  // StockService 의존성 주입을 위한 필드

	@GetMapping("/{page}")
	public ResponseEntity<Page<StockShowDTO>> getStocks(Pageable pageable) {
		Page<StockShowDTO> stockShowDTOList = stockService.getStock(pageable);
		return ResponseEntity.ok(stockShowDTOList);  // 조회된 데이터와 HTTP 상태 코드를 응답으로 반환
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> exportStock(@PathVariable(name = "id") Long id,
		@PathVariable(name = "orderId") Long orderId) {
		stockService.releaseStock(id, orderId);
		return new ResponseEntity<>(HttpStatus.OK);  // 처리 성공시 OK 응답 반환
	}

	/*재고 입고 현황 조회  그래프 연결*/
	@GetMapping("/graphimport")
	public ResponseEntity<?> getRawStockGraphimport() {
		try {
			return new ResponseEntity<>(stockService.getGraphStock(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // 예외 발생 시 BAD_REQUEST 반환
		}
	}


	/*재고 출고 현황 조회 그래프 연결*/
	@GetMapping("/graphexport")
	public ResponseEntity<?> getRawStockGraphexport() {

		try {
			return new ResponseEntity<>(stockService.getGraphStockexport(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // 예외 발생 시 BAD_REQUEST 반환
		}
	}

	/*재고 현황 엑셀 다운로드*/
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

	// 페이지네이션을 적용하여 재고 목록을 조회하는 메서드
	@GetMapping("/list/{page}")
	public String getStocks(
		@PathVariable(name = "page") Optional<Integer> page,
		@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
		Model model) {

		int currentPage = page.orElse(0);

		pageable = PageRequest.of(currentPage, 10, Sort.by("id").descending());

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

	/*출하 현황 조회*/
	@GetMapping("/shipment/{page}")
	public String getShipmentPage(
		@PathVariable(name = "page") Optional<Integer> page,
		@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
		Model model) {

		int currentPage = page.orElse(0);

		pageable = PageRequest.of(currentPage, 10, Sort.by("id").descending());
		try {
			Page<ShipmentListDTO> shipments = stockService.getShip(pageable); // Adjust page size as needed
			model.addAttribute("shipments", shipments.getContent());
			model.addAttribute("currentPage", currentPage);
			model.addAttribute("totalPages", shipments.getTotalPages());

			return "ChulHa"; // Corresponds to shipment.html
		} catch (Exception e) {
			log.error("Error fetching shipment data: " + e.getMessage());
			return "ChulHa"; // Handle error page
		}
	}

}
