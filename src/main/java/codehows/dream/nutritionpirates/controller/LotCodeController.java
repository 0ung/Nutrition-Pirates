package codehows.dream.nutritionpirates.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import codehows.dream.nutritionpirates.dto.WorkPlanDetailDTO;
import codehows.dream.nutritionpirates.service.LotCodeService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lot")
public class LotCodeController {

	private final LotCodeService lotCodeService;

	@GetMapping("/prev/{lotCode}")
	public void getPrevLotCode(@PathVariable(name = "lotCode") String lotCode, Model model) {
		List<String> list = lotCodeService.getPrevLotcode(lotCode, new ArrayList<>());
		model.addAttribute("list", list);
	}

	@GetMapping("/detail/{lotCode}")
	public void getDetail(@PathVariable(name = "lotCode") String lotCode, Model model) {
		WorkPlanDetailDTO workPlanDetailDTO = lotCodeService.getLotCodeData(lotCode);
		model.addAttribute("DTO", workPlanDetailDTO);
	}

	@GetMapping("/list/{page}")
	public void getList(@PathVariable(name = "page") Optional<Integer> page, Model model) {
		int currentPage = page.orElse(0);
		Pageable pageable = PageRequest.of(currentPage, 10);
		lotCodeService.getLotCode(pageable);
	}
}
