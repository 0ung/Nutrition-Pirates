package codehows.dream.nutritionpirates.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import codehows.dream.nutritionpirates.dto.FacilityOutPutDTO;
import codehows.dream.nutritionpirates.dto.FacilityStatusDTO;
import codehows.dream.nutritionpirates.dto.WorkPlanMainCapaDTO;
import codehows.dream.nutritionpirates.dto.WorkPlanMainDTO;
import codehows.dream.nutritionpirates.service.FacilityService;
import codehows.dream.nutritionpirates.service.ProcessPlanService;
import codehows.dream.nutritionpirates.service.RawOrderInsertService;
import codehows.dream.nutritionpirates.service.StockService;
import codehows.dream.nutritionpirates.service.WorkPlanService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
	private final FacilityService facilityService;
	private final ProcessPlanService processPlanService;
	private final WorkPlanService workPlanService;
	private final StockService stockService;
	private final RawOrderInsertService rawOrderInsertService;

	@GetMapping("/{page}")
	public String getMainPage(@PathVariable(name = "page") int page, Model model) {
		Pageable pageable = PageRequest.of(page, 5);
		//일일 생산량
		List<FacilityOutPutDTO> dailyList = facilityService.getDailyOutPut();
		model.addAttribute("daily", dailyList);

		//월간 생산량
		List<FacilityOutPutDTO> monthlyList = facilityService.getMonthlyOutPut();
		model.addAttribute("monthly", monthlyList);

		//장비 현황 생산량
		List<FacilityStatusDTO> statusList = facilityService.getFacilityStatus();
		model.addAttribute("status", statusList);

		//생산완료 예정
		List<WorkPlanMainDTO> list = processPlanService.getExpectDoneProcess(pageable);
		model.addAttribute("expect", list);

		//일일 제종 현황 및 공정별 capa
		//번호
		List<WorkPlanMainCapaDTO> list1 = workPlanService.getCapa(pageable);
		model.addAttribute("capa", list1);

		//완제품 재고 보유 현황
		model.addAttribute("product", stockService.getGraphStock());

		//원자재 보유 현황
		model.addAttribute("raws", rawOrderInsertService.getRawStockGraph());

		//원자재 발주 현황.
		model.addAttribute("rawsOrder", rawOrderInsertService.getMinus());

		return "main";
	}

	@GetMapping("/suzu")
	public void suzu() {
	}
}
