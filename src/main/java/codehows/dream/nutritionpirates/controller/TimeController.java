package codehows.dream.nutritionpirates.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import codehows.dream.nutritionpirates.service.ProgramTimeService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/time")
public class TimeController {
	private final ProgramTimeService programTimeService;

	@GetMapping("")
	public ResponseEntity<?> getTime() {
		try {
			return new ResponseEntity<>(programTimeService.getProgramTime(), HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PatchMapping("/{hour}")
	public ResponseEntity<?> updateTime(@PathVariable(name = "hour") int hour) {
		try {
			return new ResponseEntity<>(programTimeService.updateHourProgramTime(hour), HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}
