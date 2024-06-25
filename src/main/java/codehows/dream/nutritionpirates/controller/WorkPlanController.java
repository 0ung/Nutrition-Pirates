package codehows.dream.nutritionpirates.controller;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import jakarta.servlet.http.HttpServletResponse;
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

	@GetMapping("/{id}")
	public void getWorkPlanDetail(@PathVariable(name = "id") Long id, Model model) {
		try {
			WorkPlan plan = workPlanService.getWorkDetail(id);
			model.addAttribute("workplan", plan);
		} catch (Exception e) {
			model.addAttribute("error", "잘못된 요청");
		}
	}

	@GetMapping("/execute/{id}")
	public ResponseEntity<?> executeWorkPlan(@PathVariable(name = "id") Long id, String worker) {
		try {
			return new ResponseEntity<>(workPlanService.executeWork(id, worker), HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{page}")
	public void getDoneWorkPlan(@PathVariable(name = "page") Optional<Integer> page, Model model) {
		Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);
		try {
			model.addAttribute("List", workPlanService.getWorkPlanData(pageable));
		} catch (Exception e) {
			model.addAttribute("error", "잘못된 요청");
		}
	}
	@GetMapping("/history")
	public void getWorkPlanExcel(HttpServletResponse response){
		try{
			Workbook workbook = workPlanService.getHistory();
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

			String fileName = "작업지시 조회 내역.xlsx";
			String encodedFileName = java.net.	URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");

			// Set headers for different browsers
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

			workbook.write(response.getOutputStream());
			workbook.close();
		}catch (Exception e){
			log.error(e.getMessage());
		}
	}
}
