package codehows.dream.nutritionpirates.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FacilitiesController {
    @GetMapping(value = "/productionamount")
    public String index1() {
        return "productionamount";
    }
}
