package codehows.dream.nutritionpirates.controller;


import codehows.dream.nutritionpirates.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class StockController {

    private final StockService stockService;
}
