package codehows.dream.nutritionpirates.controller;


import codehows.dream.nutritionpirates.dto.*;
import codehows.dream.nutritionpirates.repository.OrderRepository;
import codehows.dream.nutritionpirates.service.OrderService;
import codehows.dream.nutritionpirates.service.RawOrderInsertService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class RawRegisterController {

    private final RawOrderInsertService rawOrderInsertService;
  /*  private final OrderService orderService;
    private final OrderRepository orderRepository;*/

    public RawRegisterController(RawOrderInsertService rawOrderInsertService) {
        this.rawOrderInsertService = rawOrderInsertService;
    }



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


    /*원자재 발주 관리 입고 테이블 */
    @GetMapping("/list/{page}")
    public String getRawStockList(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        // 페이지 번호를 설정합니다. 페이지 번호가 제공되지 않은 경우 기본값 0을 사용합니다.
        int currentPage = page;

        try {
            // 서비스 메서드를 호출하여 데이터를 가져옵니다.
            Page<RawsListDTO> rawStockPage = rawOrderInsertService.getRawStockList(pageable);

            // 모델에 데이터를 추가합니다.
            model.addAttribute("list", rawStockPage.getContent());
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("totalPages", rawStockPage.getTotalPages());

            return "orderermng";
        } catch (Exception e) {
            // 에러가 발생한 경우 로그를 기록하고 에러 페이지를 반환합니다.
            log.error(e.getMessage());
            return "error";
        }
    }
    /*@GetMapping("/list/{page}")
    public ResponseEntity<?> getRawStockList(Pageable pageable) {
        try {
            return new ResponseEntity<>(rawOrderInsertService.getRawOrderList(pageable), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }*/


    /*원자재 발주 관리 입고 엑셀 다운로드*/
    @GetMapping("/rawhistory")
    public ResponseEntity<?> getrawExcel(HttpServletResponse response) {
        try {
            Workbook workbook = rawOrderInsertService.getHistoryRaw();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            String fileName = "원자재 발주 현황 내역.xlsx";
            String encodeFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");

            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodeFileName);

            workbook.write(response.getOutputStream());
            workbook.close();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /*발주계획서*/
    @GetMapping("/calculate")
    public ResponseEntity<?> getMinus() {
        return new ResponseEntity<>(rawOrderInsertService.getMinus(), HttpStatus.OK);
    }

    /*입출고관리 페이징 연결*/
    //페이징 기능
  /*  @GetMapping("/rawstock/{page}")
    public String getRawStockList(@PathVariable(name = "page") Optional<Integer> page, @PageableDefault(page=0,size = 10,sort = "id",
            direction = Sort.Direction.DESC) Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);
        try {
            model.addAttribute("list", rawOrderInsertService.getRawStockList(pageable));
            return "rawmng";
        } catch (Exception e) {
            log.error(e.getMessage());
            return "error";
        }
    }*/

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


    /* 3일 이하 원자재 */
    /* 3일 이하 원자재 */
    @GetMapping("/rawperiod")
    public ResponseEntity<List<RawPeriodDTO>> getPeriodList(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            List<RawPeriodDTO> periodList = rawOrderInsertService.getPeriodList(pageable);
            return ResponseEntity.ok().body(periodList);
        } catch (Exception e) {
            log.error("Error retrieving period list: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

// 3일 이하 원자재
public List<RawPeriodDTO> getPeriodList(Pageable pageable) {

    // 현재 프로그램 시간을 가져옵니다.
    Timestamp timestamp = programTimeService.getProgramTime().getCurrentProgramTime();
    LocalDateTime currentDate = timestamp.toLocalDateTime();

    // 현재 시간에서 3일 전 시간을 계산합니다.
    LocalDateTime minDate = currentDate.minusDays(3);
    Timestamp minTimestamp = Timestamp.valueOf(minDate);

    // IMPORT 상태이고, deadline이 minTimestamp와 timestamp 사이인 데이터를 페이징하여 가져옵니다.
    Page<Raws> pages = rawRepository.findByStatusAndDeadlineBetween(
            Status.IMPORT,
            minTimestamp,
            timestamp,
            pageable);

    // 결과를 담을 리스트를 초기화합니다.
    List<RawPeriodDTO> list = new ArrayList<>();

    // 가져온 데이터를 RawPeriodDTO로 변환하여 리스트에 추가합니다.
    pages.forEach((e) -> {
        list.add(RawPeriodDTO.builder()
                .rawsCode(e.getRawsCode())
                .product(e.getProduct().getValue())
                .importDate(new Date(e.getImportDate().getTime()))
                .deadLine(new Date(e.getDeadLine().getTime()))
                .quantity(e.getQuantity())
                .build());
    });

    return list;
}

  /*  @GetMapping("/rawperiod/{page}")
    public ResponseEntity<?> getPeriodList(@PathVariable(name = "page") Optional<Integer> page) {

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);

        try {
            return new ResponseEntity<>(rawOrderInsertService.getPeriodList(pageable), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }*/

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