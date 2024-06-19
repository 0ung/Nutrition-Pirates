package codehows.dream.nutritionpirates.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class LOTController {
    @GetMapping(value = {"/lot", "/LOT"})
    public String getChulHa(){
        return "LOT";
    }
}
