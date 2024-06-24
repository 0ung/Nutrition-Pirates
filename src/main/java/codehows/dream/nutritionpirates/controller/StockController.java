package codehows.dream.nutritionpirates.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StockController {
    @GetMapping(value = "/stockcheck")
    public String index1() {
        return "stockCheck";
    }

    @GetMapping(value = "/stockcs")
    public String index2() {
        return "stockCS";
    }

}