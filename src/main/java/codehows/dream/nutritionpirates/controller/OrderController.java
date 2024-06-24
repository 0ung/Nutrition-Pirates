package codehows.dream.nutritionpirates.controller;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import codehows.dream.nutritionpirates.dto.MesOrderInsertDTO;
import codehows.dream.nutritionpirates.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

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
	public String getList(@PathVariable(name = "page") Optional<Integer> page, Model model) {
		int currentPage = page.orElse(0);
		Pageable pageable = PageRequest.of(currentPage, 10);

		try {
			model.addAttribute("dto", orderService.getOrderList(pageable));
			model.addAttribute("currentPage", currentPage);
			model.addAttribute("totalPages", orderService.getTotalPages());
			return "/suzu_check";

		} catch (Exception e) {
			log.error(e.getMessage());
			return "error";
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

	@GetMapping("/orderer/all")
	public ResponseEntity<?> getOrderer() {
		try {
			return new ResponseEntity<>(orderService.getOrderer(), HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}


	@GetMapping("/orderer/{page}")
	public String getList2(@PathVariable(name = "page") Optional<Integer> page, Model model) {
		int currentPage = page.orElse(0);
		Pageable pageable = PageRequest.of(currentPage, 10);

		try {
			model.addAttribute("dto2", orderService.getOrderer(pageable));
			model.addAttribute("currentPage", currentPage);
			model.addAttribute("totalPages", orderService.getTotalPages());
			return "/suzu_check_orderer";

		} catch (Exception e) {
			log.error(e.getMessage());
			return "error";
		}
	}



}
