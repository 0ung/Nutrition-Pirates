package codehows.dream.nutritionpirates.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SystemTimeController {
    @GetMapping(value = "/systemtime")
    public String index() {
        return "systemtime";
    }


}