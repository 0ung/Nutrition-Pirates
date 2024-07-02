package codehows.dream.nutritionpirates.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public String getFacilityStatus(Model model) {
		List<FacilityStatusDTO> list = facilityService.getFacilityStatus();
		model.addAttribute("list", list);

		return "productionamount";
	}

	@GetMapping("/daily")
	public String getFacilityDaily(Model model) {
		Map<String, String> map = new HashMap<>();
		map.put("title", "일일 생산량");
		List<FacilityOutPutDTO> list = facilityService.getDailyOutPut();
		model.addAttribute("list", list);
		model.addAttribute("data", map);

		return "productionVolume";
	}

	@GetMapping("/monthly")
	public String getFacilityMonthly(Model model) {
		Map<String, String> map = new HashMap<>();
		map.put("title", "월간 생산량");
		List<FacilityOutPutDTO> list = facilityService.getMonthlyOutPut();
		model.addAttribute("list", list);
		model.addAttribute("data", map);

		return "productionVolume";
	}
}
