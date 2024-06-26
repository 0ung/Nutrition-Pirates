package codehows.dream.nutritionpirates.controller;

import codehows.dream.nutritionpirates.dto.WorkPlanDTO;
import codehows.dream.nutritionpirates.dto.WorkPlanDetailDTO;
import java.util.Optional;

import codehows.dream.nutritionpirates.dto.WorkPlanListDTO;
import codehows.dream.nutritionpirates.entity.Orderer;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
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

	@GetMapping("/list/{page}")
	public String getDoneWorkPlan(@PathVariable(name = "page") Optional<Integer> page, Model model) {
		int currentPage = page.orElse(0);
		Pageable pageable = PageRequest.of(currentPage, 10);
		try {
			Page<WorkPlanListDTO> list =  workPlanService.getWorkPlanData(pageable);
			model.addAttribute("List", list);// Set the content, not the repository itself
			model.addAttribute("currentPage", currentPage);
			model.addAttribute("totalPages", list.getTotalPages());
			return "SengSan_check";
		} catch (Exception e) {
			model.addAttribute("error", "잘못된 요청");
			return "error";
		}
	}


	@GetMapping("/history")
	public ResponseEntity<?> getWorkPlanExcel(HttpServletResponse response){
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
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (Exception e){
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
