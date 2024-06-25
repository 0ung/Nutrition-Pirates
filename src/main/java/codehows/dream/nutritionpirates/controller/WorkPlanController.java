package codehows.dream.nutritionpirates.controller;

import codehows.dream.nutritionpirates.dto.WorkPlanDTO;
import codehows.dream.nutritionpirates.dto.WorkPlanDetailDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import codehows.dream.nutritionpirates.constants.Facility;
import codehows.dream.nutritionpirates.dto.ActivateFacilityDTO;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.service.WorkPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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

	@GetMapping("/{id}")
	public ResponseEntity<WorkPlanDetailDTO> getWorkPlanDetail(@PathVariable(name = "id") Long id, Model model) {
		try {
			WorkPlanDetailDTO plan = workPlanService.getWorkDetail(id);
			return new ResponseEntity<>(plan,HttpStatus.OK);
		} catch (Exception e) {
			model.addAttribute("error", "잘못된 요청");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}


	@GetMapping("/execute/{id}")
	public ResponseEntity<?> executeWorkPlan(@PathVariable(name = "id") Long id, @RequestParam(name = "worker") String worker) {
		try {
			return new ResponseEntity<>(workPlanService.executeWork(id, worker), HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}



}
