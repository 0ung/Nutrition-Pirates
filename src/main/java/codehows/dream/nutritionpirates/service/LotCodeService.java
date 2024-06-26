package codehows.dream.nutritionpirates.service;

import codehows.dream.nutritionpirates.entity.LotCode;
import codehows.dream.nutritionpirates.repository.LotCodeRepository;
import codehows.dream.nutritionpirates.repository.WorkPlanRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LotCodeService {

    private final WorkPlanRepository workPlanRepository;
    private final LotCodeRepository lotCodeRepository;

    public List<String> getPrevLotcode(String preLotCode, List<String> lotCodes) {
        LotCode lotCode = lotCodeRepository.findByPervLot(preLotCode);
        if(lotCode.getPervLot() == null){
            return lotCodes;
        }
        lotCodes.add(lotCode.getLotCode());
        return getPrevLotcode(lotCode.getPervLot(),lotCodes);
    }

}
