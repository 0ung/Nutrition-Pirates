package codehows.dream.nutritionpirates.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import codehows.dream.nutritionpirates.dto.WorkPlanListDTO;
import codehows.dream.nutritionpirates.entity.LotCode;
import org.springframework.data.domain.Page;
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
	@GetMapping("/prev/{lotCode}") //검색
	public void getPrevLotCode(@PathVariable(name = "lotCode") String lotCode, Model model) {
		List<String> list = lotCodeService.getPrevLotcode(lotCode, new ArrayList<>());
		model.addAttribute("list", list);
	}

	@GetMapping("/detail/{lotCode}") //클릭시 디테일
	public void getDetail(@PathVariable(name = "lotCode") String lotCode, Model model) {
		WorkPlanDetailDTO workPlanDetailDTO = lotCodeService.getLotCodeData(lotCode);
		model.addAttribute("DTO", workPlanDetailDTO);
	}

//	@GetMapping("/list/{page}")
//	public String getList(@PathVariable(name = "page") Optional<Integer> page, Model model) {
//		int currentPage = page.orElse(0);
//		Pageable pageable = PageRequest.of(currentPage, 10);
//		Page<LotCode> list = lotCodeService.getLotCode(pageable);
//		List<String> result = new ArrayList<>();
//		list.forEach(
//				e->result.add(e.getLotCode())
//		);
//		model.addAttribute("List",result);
//		model.addAttribute("currentPage", currentPage);
//		model.addAttribute("totalPages",list.getTotalPages());
//		return "LOT";
//	}



	@GetMapping("/list/{page}")
	public String getList(@PathVariable(name = "page") Optional<Integer> page, Model model) {
		int currentPage = page.orElse(0);
		Pageable pageable = PageRequest.of(currentPage, 10);
		Page<LotCode> list = lotCodeService.getLotCode(pageable);
		List<String> result = new ArrayList<>();
		list.forEach(e -> result.add(e.getLotCode()));
		model.addAttribute("list", result);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", list.getTotalPages());
		return "LOT";
	}
}
