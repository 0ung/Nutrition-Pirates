package codehows.dream.nutritionpirates.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import codehows.dream.nutritionpirates.dto.FacilityOutPutDTO;
import codehows.dream.nutritionpirates.dto.FacilityStatusDTO;
import codehows.dream.nutritionpirates.service.FacilityService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/facility")
@RequiredArgsConstructor
public class FacilitiesController {

	private final FacilityService facilityService;

	@GetMapping(value = "/productionamount")
	public String index1() {
		return "productionamount";
	}

	@GetMapping("/status")
	public String  getFacilityStatus(Model model) {
		List<FacilityStatusDTO> list = facilityService.getFacilityStatus();
		System.out.println(Arrays.toString(list.toArray()));
		model.addAttribute("list", list);

		return "/productionamount";
	}

	@GetMapping("/daily")
	public void getFacilityDaily(Model model) {
		List<FacilityOutPutDTO> list = facilityService.getDailyOutPut();
		model.addAttribute("list", list);
	}

	@GetMapping("/monthly")
	public void getFacilityMonthly(Model model){
		List<FacilityOutPutDTO> list = facilityService.getMonthlyOutPut();
		model.addAttribute("list", list);
	}

	@GetMapping("/test1")
	public String productionVolume(){
		return "/productionVolume";
	}
}
