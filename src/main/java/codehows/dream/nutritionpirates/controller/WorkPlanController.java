package codehows.dream.nutritionpirates.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.dto.ActivateFacilityDTO;
import codehows.dream.nutritionpirates.service.WorkPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequiredArgsConstructor
@RequestMapping("/workplan")
@Log4j2
public class WorkPlanController {

	private final WorkPlanService workPlanService;

	@GetMapping("/facility/activate")
	public ResponseEntity<?> getFacilityActivate() {
		Facility[] facilitys = Facility.getAllFacilities();
		try {
			ActivateFacilityDTO facilityDTO = workPlanService.getActivateFacility(facilitys);
			return new ResponseEntity<>(facilityDTO, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/execute/{id}")
	public ResponseEntity<?> executeWorkPlan(@PathVariable(name = "id") Long id) {
		try {
			return new ResponseEntity<>(workPlanService.executeWork(id), HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
