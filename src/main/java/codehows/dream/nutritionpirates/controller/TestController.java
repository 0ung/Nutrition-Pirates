package codehows.dream.nutritionpirates.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class TestController {
    @GetMapping(value = "/")
    public String index() {
        return "test";
    }
}
