package codehows.dream.nutritionpirates.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class SengSanController {

    @GetMapping(value = {"/sengsan","/sengsanCheck","/sengsancheck"})
    public String getSengsanCheck(){
        return "SengSan_check";
    }
    @GetMapping(value = {"plan","/sengsanPlan","/sengsanplan" })
    public String getSengsanPlan(){
        return "SengSan_Plan";
    }
}
