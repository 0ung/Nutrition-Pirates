package codehows.dream.nutritionpirates.controller;

import java.util.Optional;

import codehows.dream.nutritionpirates.dto.RawOrderListDTO;
import codehows.dream.nutritionpirates.dto.RawPeriodDTO;
import codehows.dream.nutritionpirates.dto.RawsListDTO;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import codehows.dream.nutritionpirates.dto.RawOrderInsertDTO;
import codehows.dream.nutritionpirates.repository.OrderRepository;
import codehows.dream.nutritionpirates.service.OrderService;
import codehows.dream.nutritionpirates.service.RawOrderInsertService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class RawRegisterController {

    private final RawOrderInsertService rawOrderInsertService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    /*원자재 발주관리 발주등록버튼 선택지 연결*/
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

    @GetMapping("/bom")
    public ResponseEntity<?> calculateBOMs() {
        return new ResponseEntity<>(rawOrderInsertService.calculateBOMs(), HttpStatus.OK);
    }

    /*발주계획서*/
    @GetMapping("/calculate")
    public ResponseEntity<?> getMinus() {
        return new ResponseEntity<>(rawOrderInsertService.getMinus(), HttpStatus.OK);
    }

    @GetMapping("/raworder/{page}")
    public String getRawOrderList(
            @PathVariable(name = "page") Optional<Integer> page,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        int currentPage = page.orElse(0);

        pageable = PageRequest.of(currentPage, 10, Sort.by("id").descending());

        try {
            Page<RawOrderListDTO> orderPage = rawOrderInsertService.getRawOrderList(pageable);

            model.addAttribute("list", orderPage.getContent());
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("totalPages", orderPage.getTotalPages());

            return "orderermng";
        } catch (Exception e) {
            // 에러가 발생한 경우 로그를 기록하고 에러 페이지를 반환합니다.
            log.error(e.getMessage());
            return "error";
        }
    }

    @GetMapping("/rawstock/{page}")
    public String getRawStockList(
            @PathVariable(name = "page") Optional<Integer> page,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        // 페이지 번호를 설정합니다. 페이지 번호가 제공되지 않은 경우 기본값 0을 사용합니다.
        int currentPage = page.orElse(0);

        // 페이지 번호를 기반으로 Pageable 객체를 생성합니다.
        pageable = PageRequest.of(currentPage, 10, Sort.by("id").descending());

        try {
            // 서비스 메서드를 호출하여 데이터를 가져옵니다.
            Page<RawsListDTO> rawStockPage = rawOrderInsertService.getRawStockList(pageable);

            // 모델에 데이터를 추가합니다.
            model.addAttribute("list", rawStockPage.getContent());
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("totalPages", rawStockPage.getTotalPages());

            return "rawmng";
        } catch (Exception e) {
            // 에러가 발생한 경우 로그를 기록하고 에러 페이지를 반환합니다.
            log.error(e.getMessage());
            return "error";
        }
    }

    /*원자재 현황 조회 그래프 연결*/
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

    /*3일 이하 원자재*/
    @GetMapping("/rawperiod/{page}")
    public String getPeriodList(
            @PathVariable(name = "page") Optional<Integer> page,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        int currentPage = page.orElse(0);

        pageable = PageRequest.of(currentPage, 10, Sort.by("id").descending());

        try{
            Page<RawPeriodDTO> pages = rawOrderInsertService.getPeriodList(pageable);

            // 모델에 데이터를 추가합니다.
            model.addAttribute("list", pages.getContent());
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("totalPages", pages.getTotalPages());

            return "rawcs";

        } catch (Exception e) {
            // 에러가 발생한 경우 로그를 기록하고 에러 페이지를 반환합니다.
            log.error(e.getMessage());
            return "error";
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

    @GetMapping("/orderhistory")
    public ResponseEntity<?> getorderExcel(HttpServletResponse response) {
        try {
            Workbook workbook = rawOrderInsertService.getHistoryRaw();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            String fileName = "원자재 발주 현황 내역.xlsx";
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