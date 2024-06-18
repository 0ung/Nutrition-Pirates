package codehows.dream.nutritionpirates.controller;

import codehows.dream.nutritionpirates.dto.RawBOMDTO;
import codehows.dream.nutritionpirates.service.BOMCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bom")
public class BOMCalculatorController {

    @Autowired
    private BOMCalculatorService service;

    @GetMapping("/garlic")
    public RawBOMDTO calculateGarlicBOM(@RequestParam int quantity) {
        return service.garlicJuiceBOMCal(quantity);
    }

    @GetMapping("/cabbage")
    public RawBOMDTO calculateCabbageBOM(@RequestParam int quantity) {
        return service.cabbageJuiceBOMCal(quantity);
    }

    @GetMapping("/pomegranate")
    public RawBOMDTO calculatePomegranateBOM(@RequestParam int quantity) {
        return service.pomegranateStickBOMCal(quantity);
    }

    @GetMapping("/plum")
    public RawBOMDTO calculatePlumBOM(@RequestParam int quantity) {
        return service.plumStickBOMCal(quantity);
    }
}

