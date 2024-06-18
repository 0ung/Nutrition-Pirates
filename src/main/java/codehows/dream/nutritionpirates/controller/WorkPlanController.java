package codehows.dream.nutritionpirates.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.dto.ActivateFacilityDTO;
import codehows.dream.nutritionpirates.service.WorkPlanService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WorkPlanController {

	private final WorkPlanService workPlanService;

	public ResponseEntity<?> getFacilityActivate() {
		Facility[] facilitys = Facility.getAllFacilities();
		try {
			ActivateFacilityDTO facilityDTO = workPlanService.getActivateFacility(facilitys);
			return new ResponseEntity<>(facilityDTO, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
