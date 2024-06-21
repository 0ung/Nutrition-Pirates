package codehows.dream.nutritionpirates.controller;


import codehows.dream.nutritionpirates.dto.*;
import codehows.dream.nutritionpirates.entity.Order;
import codehows.dream.nutritionpirates.repository.OrderRepository;
import codehows.dream.nutritionpirates.service.BOMCalculatorService;
import codehows.dream.nutritionpirates.service.OrderService;
import codehows.dream.nutritionpirates.service.RawOrderInsertService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class RawRegisterController {

    private final RawOrderInsertService rawOrderInsertService;
    private final OrderService orderService;
    private final BOMCalculatorService bomCalculatorService;
    private final OrderRepository orderRepository;

    @PostMapping("/register")
    public ResponseEntity<?> insertRawOrder(@RequestBody RawOrderInsertDTO rawOrderInsertDTO) {
        try {
            rawOrderInsertService.insert(rawOrderInsertDTO);
            return new ResponseEntity<>("Raw order inserted successfully", HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{rawsCode}")
    public ResponseEntity<?> importRaw(@PathVariable(name = "rawsCode") String rawsCode) {
        rawOrderInsertService.rawImport(rawsCode);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{rawsCode}")
    public ResponseEntity<?> exportRaw(@PathVariable(name = "rawsCode") String rawsCode) {
        rawOrderInsertService.rawExport(rawsCode);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*@GetMapping("/{page}")
    public ResponseEntity<?> getList(@PathVariable(name = "page") Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);

        try {
            return new ResponseEntity<>(rawOrderInsertService.getRawOrderList(pageable), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
*/
    @GetMapping("/rawstock/{page}")
    public ResponseEntity<?> getRawStockList(@PathVariable(name = "page") Optional<Integer> page) {

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);

        try {
            return new ResponseEntity<>(rawOrderInsertService.getRawStockList(pageable), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 이건 뭐지 ??????
    @GetMapping("/rawplan")
    public ResponseEntity<?> getRawsPlan() {
        return new ResponseEntity<>(rawOrderInsertService.getRawsPlanDTO(), HttpStatus.OK);
    }
    @GetMapping("/bom")
    public ResponseEntity<?> calculateBOMs() {
        return new ResponseEntity<>(rawOrderInsertService.calculateBOMs(), HttpStatus.OK);
    }
    @GetMapping("/calculate")
    public ResponseEntity<?> getMinus() {
        return new ResponseEntity<>(rawOrderInsertService.getMinus(), HttpStatus.OK);
    }

    /*@GetMapping("/rawperiod/{page}")
    public ResponseEntity<?> getPeriodList(@PathVariable(name = "page") Optional<Integer> page) {

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0,10);

        try {
            return new ResponseEntity<>(rawGraphService.getPeriodList(pageable), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }*/

    @GetMapping("/graph")
    public ResponseEntity<?> getRawStockGraph() {

        try {
            return new ResponseEntity<>(rawOrderInsertService.getRawStockGraph(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/notifications")
    public ResponseEntity<?> getMinimumStockNotifications() {

        try {
            return new ResponseEntity<>(rawOrderInsertService.checkMinimumStock(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getExcel(HttpServletResponse response) {
        try {
            Workbook workbook = rawOrderInsertService.getHistory();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            String fileName = "원자재 현황 내역.xlsx";
            String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");

            // Set headers for different browsers
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

            workbook.write(response.getOutputStream());
            workbook.close();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
