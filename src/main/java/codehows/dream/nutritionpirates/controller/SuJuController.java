package codehows.dream.nutritionpirates.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class SuJuController {

    @GetMapping(value = {"/suju", "/suzu"})
    public String getSuju() {
        return "suzu";
    }
    @GetMapping(value = {"/suzucheck", "/sujucheck"})
    public String getSujuCheck (){
        return "suzu_check";
    }
    @GetMapping(value = {"/suzucheckOrderer", "/order/suzu_check_orderer"})
    public String getSujuCheckOrderer (){
        return "suzu_check_orderer";
    }
}
