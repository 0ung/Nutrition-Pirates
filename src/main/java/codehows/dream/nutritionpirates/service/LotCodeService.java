package codehows.dream.nutritionpirates.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import codehows.dream.nutritionpirates.dto.WorkPlanDetailDTO;
import codehows.dream.nutritionpirates.entity.LotCode;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.repository.LotCodeRepository;
import codehows.dream.nutritionpirates.repository.WorkPlanRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LotCodeService {

	private final WorkPlanRepository workPlanRepository;
	private final LotCodeRepository lotCodeRepository;
	private final ProgramTimeService programTimeService;

	public List<String> getPrevLotcode(String lotCode, List<String> lotCodes) {
		if (lotCode == null) {
			return lotCodes;
		}
		LotCode code = lotCodeRepository.findById(lotCode).orElse(null);
		lotCodes.add(code.getLotCode());
		return getPrevLotcode(code.getPervLot(), lotCodes);
	}

	public WorkPlanDetailDTO getLotCodeData(String lotCode) {
		LotCode code = lotCodeRepository.findById(lotCode).orElse(null);
		if (code == null) {
			throw new IllegalArgumentException();
		}
		WorkPlan plan = workPlanRepository.findByLotCodeId(lotCode);
		return WorkPlanDetailDTO.toWorkPlanDetailDTO(plan, programTimeService.getProgramTime().getCurrentProgramTime());
	}

	public List<String> getLotCode(Pageable pageable) {
		Page<LotCode> a8code = lotCodeRepository.findByLotCodeContaining("A8", "B7", pageable);
		List<String> list = new ArrayList<>();
		a8code.forEach(
			e->list.add(e.getLotCode())
		);
		return list;
	}

}
