package codehows.dream.nutritionpirates.controller;

import codehows.dream.nutritionpirates.service.LotCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lot")
public class LotCodeController {

    private final LotCodeService lotCodeService;



    @GetMapping("/prev/{prevCode}")
    public void getPrevLotCode(@PathVariable(name = "prevCode") String prevCode){
       List<String> list =  lotCodeService.getPrevLotcode(prevCode,new ArrayList<>());
    }

}
