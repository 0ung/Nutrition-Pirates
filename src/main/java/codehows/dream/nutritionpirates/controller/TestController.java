package codehows.dream.nutritionpirates.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    @GetMapping(value = "/")
    public String index() {
        return "main";
    }

    @GetMapping(value = {"/chulha", "/ChulHa","/Chulha"})
    public String getChulHa(){
        return "ChulHa";
    }

    @GetMapping(value = {"/suju", "/suzu"})
    public String getSuju() {
        return "suzu";
    }
    @GetMapping(value = {"/suzucheck", "/sujucheck"})
    public String getSujuCheck (){
        return "suzu_check";
    }
    @GetMapping(value = {"/suzucheckOrderer", "/sujucheckOrderer"})
    public String getSujuCheckOrderer (){
        return "suzu_check_orderer";
    }

    @GetMapping(value = {"/lot", "/LOT"})
    public String getLot(){
        return "LOT";
    }
    @GetMapping(value = "/systemtime")
    public String index1() {
        return "systemtime";
    }

    @GetMapping(value = "/sengsanPlan")
    public String inasdas(){
        return "SengSan_Plan";
    }


    @GetMapping(value = "/sengsancheck")
    public String sengsancheck(){
        return "SengSan_check";
    }

}