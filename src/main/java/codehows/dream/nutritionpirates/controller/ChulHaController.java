package codehows.dream.nutritionpirates.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChulHaController {
    @GetMapping(value = {"/chulha", "/ChulHa","/Chulha"})
    public String getChulHa(){
        return "ChulHa";
    }
}
