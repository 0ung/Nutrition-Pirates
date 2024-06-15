package codehows.dream.nutritionpirates.controller;

import codehows.dream.nutritionpirates.dto.MesOrderInsertDTO;
import codehows.dream.nutritionpirates.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
@Log4j2
public class OrderController {

	private final OrderService orderService;

	@PostMapping("")
	public ResponseEntity<?> insertOrder(@RequestBody MesOrderInsertDTO mesOrderInsertDTO) {
		try {
			orderService.insert(mesOrderInsertDTO);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PostMapping("/excel")
	public ResponseEntity<?> insertExcel(@RequestPart(name = "excel") MultipartFile file) {
		try {
			orderService.readExcel(file);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping("/{page}")
	public ResponseEntity<?> getList(@PathVariable(name = "page") Optional<Integer> page) {
		Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);
		try {
			return new ResponseEntity<>(orderService.getOrderList(pageable), HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> cancelOrder(@PathVariable(name = "id") Long id) {
		try {
			orderService.cancelOrder(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/orderer")
	public ResponseEntity<?> getOrderer() {
		try {
			return new ResponseEntity<>(orderService.getOrderer(), HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/history")
	public ResponseEntity<?> getExcel(HttpServletResponse response) {
		try {
			Workbook workbook = orderService.getHistory();
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

			String fileName = "수주 내역.xlsx";
			String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");

			// Set headers for different browsers
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

			workbook.write(response.getOutputStream());
			workbook.close();
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}


}
