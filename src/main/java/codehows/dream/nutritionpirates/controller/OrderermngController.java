package codehows.dream.nutritionpirates.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderermngController {
    @GetMapping(value = "/orderermng")
    public String index1() {
        return "orderermng";
    }

    @GetMapping(value = "/rawmng")
    public String index2() {
        return "rawmng";
    }

    @GetMapping(value = "/rawcs")
    public String index3() {
        return "rawcs";
    }
}
