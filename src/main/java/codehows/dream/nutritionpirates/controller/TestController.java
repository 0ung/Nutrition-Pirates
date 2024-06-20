package codehows.dream.nutritionpirates.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class TestController {
    @GetMapping(value = "/")
    public String index() {
        return "index";
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

    @GetMapping(value = "/stockcheck")
    public String index3() {
        return "stockCheck";
    }

    @GetMapping(value = "/stockcs")
    public String index2() {
        return "stockCS";
    }

    @GetMapping(value = {"/sengsan","/sengsanCheck","/sengsancheck"})
    public String getSengsanCheck(){
        return "SengSan_check";
    }
    @GetMapping(value = {"plan","/sengsanPlan","/sengsanplan"})
    public String getSengsanPlan(){
        return "SengSan_Plan";
    }

    @GetMapping(value = "/orderermng")
    public String index11() {
        return "orderermng";
    }

    @GetMapping(value = "/rawmng")
    public String index21() {
        return "rawmng";
    }

    @GetMapping(value = "/rawcs")
    public String index31() {
        return "rawcs";
    }
}
