package codehows.dream.nutritionpirates.service;


import codehows.dream.nutritionpirates.constants.RawProductName;
import codehows.dream.nutritionpirates.constants.ProductName;
import codehows.dream.nutritionpirates.constants.RawsReason;
import codehows.dream.nutritionpirates.constants.Status;
import codehows.dream.nutritionpirates.dto.*;
import codehows.dream.nutritionpirates.entity.Order;
import codehows.dream.nutritionpirates.entity.Orderer;
import codehows.dream.nutritionpirates.entity.Raws;
import codehows.dream.nutritionpirates.repository.OrderRepository;
import codehows.dream.nutritionpirates.repository.RawRepository;
import groovy.lang.Lazy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Log4j2
public class RawOrderInsertService {

    private final RawRepository rawRepository;
    private final OrderRepository orderRepository;
    private final ProgramTimeService programTimeService;



    public List<RawOrderPlanDTO> getRawsPlanDTO() {
        List<Order> orders = orderRepository.findAll();
        List<RawOrderPlanDTO> rawOrderPlanDTOS = orders.stream().map(order -> RawOrderPlanDTO.builder()
                        //.product(order.getProduct())
                        .quantity(order.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return rawOrderPlanDTOS;
    }

    // 영업일 확인 메서드 (토요일, 일요일, 공휴일을 제외)
    private boolean isBusinessDay(LocalDate date) {
        // 주말 제외
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return false;
        }
        // 공휴일 제외 (여기서는 예시로 몇 개의 날짜를 공휴일로 지정)
        List<LocalDate> holidays = Arrays.asList(
                LocalDate.of(date.getYear(), 1, 1),  // 새해
                LocalDate.of(date.getYear(),2,10), // 설날
                LocalDate.of(date.getYear(),3,1), // 삼일절
                LocalDate.of(date.getYear(),5,5), // 어린이날
                LocalDate.of(date.getYear(),5,15), // 부처님오시는날
                LocalDate.of(date.getYear(),6,6), // 현출일
                LocalDate.of(date.getYear(),8,15), // 광복절
                LocalDate.of(date.getYear(),9,16), // 추석연휴
                LocalDate.of(date.getYear(),9,17), // 추석연휴
                LocalDate.of(date.getYear(),9,18), // 추석연휴
                LocalDate.of(date.getYear(),10,3), // 개천절
                LocalDate.of(date.getYear(),10,9), // 한글날
                LocalDate.of(date.getYear(), 12, 25) // 크리스마스
                // 추가 공휴일을 여기에 추가
        );

        if (holidays.contains(date)) {
            return false;
        }
        return true;
    }
    // 영업일 및 공휴일을 고려하여 예상 입고일 계산
    private LocalDate addBusinessDaysSwitch(LocalDate startDate, int businessDays) {
        LocalDate date = startDate;
        int daysAdded = 0;
        // switch 구문을 사용하여 각 날짜에 대해 영업일 계산
        switch (businessDays) {
            case 2:
                while (daysAdded < 2) {
                    date = date.plusDays(1);
                    if (isBusinessDay(date)) {
                        daysAdded++;
                    }
                }
                break;
            case 3:
                while (daysAdded < 3) {
                    date = date.plusDays(1);
                    if (isBusinessDay(date)) {
                        daysAdded++;
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + businessDays);
        }
        return date;
    }

    private Timestamp calculateExpectedImportDate(Timestamp orderDateTime) {
        LocalDateTime orderDateTimeLocal = orderDateTime.toLocalDateTime();
        LocalTime time = orderDateTimeLocal.toLocalTime();
        int daysToAdd;
        // 12:00 이전, 이후에 따른 daysToAdd 설정
        if (time.isBefore(LocalTime.NOON)) {
            daysToAdd = 2;
        } else {
            daysToAdd = 3;
        }

        LocalDate date = orderDateTimeLocal.toLocalDate();
        // 날짜 계산을 위한 switch 구문
        switch (daysToAdd) {
            case 2:
                date = addBusinessDaysSwitch(date, 2);
                break;
            case 3:
                date = addBusinessDaysSwitch(date, 3);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + daysToAdd);
        }
        // 최종 계산된 LocalDate와 원래의 LocalTime을 결합하여 LocalDateTime을 만듦
        LocalDateTime expectedDateTime = date.atTime(time);
        // LocalDate를 Timestamp로 변환
        return Timestamp.valueOf(expectedDateTime);
    }

    public void insert(RawOrderInsertDTO rawOrderInsertDTO) {
        validateQuantity(rawOrderInsertDTO);

        Timestamp timestamp = programTimeService.getProgramTime().getCurrentProgramTime();

        // calculateExpectedImportDate 메서드 호출
        Timestamp expectedImportDateTimestamp = calculateExpectedImportDate(timestamp);


        String rawsCode = createRawsCodes(rawOrderInsertDTO);
        Raws raws = Raws.builder()
                .product(rawOrderInsertDTO.getProduct())
                .rawsCode(rawsCode)
                .quantity(rawOrderInsertDTO.getQuantity())
                .partner(rawOrderInsertDTO.getPartner())
                .orderDate(timestamp)
                .expectedImportDate(expectedImportDateTimestamp)
                .status(Status.WAITING)
                .build();

        rawRepository.save(raws);
    }




    // 원자재 제품 코드 약자로 구분
    private String getRaws(RawProductName productName) {
        String code;
        switch (productName) {
            case CABBAGE:
                code = "C";
                break;
            case BLACK_GARLIC:
                code = "G";
                break;
            case POMEGRANATE:
                code = "S";
                break;
            case PLUM:
                code = "P";
                break;
            case HONEY:
                code = "H";
                break;
            case COLLAGEN:
                code = "L";
                break;
            case WRAPPING_PAPER:
                code = "W";
                break;
            case BOX:
                code = "B";
                break;
            default:
                throw new IllegalArgumentException("없는 상품원자재코드 : " + productName);
        }
        return code;
    }

    // 원자재 코드 생성 날짜-투입량-원자재약자 ex yyyyMMdd1000C - 시간적용 완료
    private String createRawsCodes(RawOrderInsertDTO rawOrderInsertDTO) {

        Timestamp timestamp = programTimeService.getProgramTime().getCurrentProgramTime();
        LocalDateTime now = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        //LocalDateTime now = LocalDateTime.now();
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = now.format(formatter);

        int quantity = rawOrderInsertDTO.getQuantity();
        String rawCode = getRaws(rawOrderInsertDTO.getProduct());

        String rawsCode = formattedDate + rawCode + quantity;

        return rawsCode;
    }

    public void validateQuantity(RawOrderInsertDTO rawOrderInsertDTO) {
        int quantity = rawOrderInsertDTO.getQuantity();
        RawProductName product = rawOrderInsertDTO.getProduct();

        switch (product) {
            case CABBAGE:
            case BLACK_GARLIC:
                if (quantity < 100 || quantity > 2000) {
                    throw new IllegalArgumentException(product.getValue() + " : 100KG 에서 2000KG 사이로 주문하세요.");
                }
                break;
            case POMEGRANATE:
            case PLUM:
            case COLLAGEN:
                if (quantity < 20 || quantity > 200) {
                    throw new IllegalArgumentException(product.getValue() + " : 20L에서 200L 사이로 주문하세요.");
                }
                break;
            case HONEY:
                if (quantity < 1 || quantity > 200) {
                    throw new IllegalArgumentException(product.getValue() + " : 1L에서 200L 사이로 주문하세요.");
                }
                break;
            case WRAPPING_PAPER:
                if (quantity < 10000 || quantity > 100000) {
                    throw new IllegalArgumentException(product.getValue() + " : 10000개에서 100000개 사이로 주문하세요.");
                }
                break;
            case BOX:
                if (quantity < 1000 || quantity > 10000) {
                    throw new IllegalArgumentException(product.getValue() + " : 1000개에서 10000개 사이로 주문하세요.");
                }
                break;
            default:
                throw new IllegalArgumentException("상품명 오류 " + product.getValue());
        }
    }

    public void rawImport(Long id) {

        Raws raw = rawRepository.findById(id).orElse(null);

        // 프로그램 시간설정 적용
        Timestamp timestamp = programTimeService.getProgramTime().getCurrentProgramTime();
        Date importDate = new Date(timestamp.getTime());

        // deadLine 계산
        LocalDateTime deadLineDateTime = timestamp.toLocalDateTime().plusDays(14);
        Timestamp deadLineTimestamp = Timestamp.valueOf(deadLineDateTime);

        raw.rawImport(importDate, Status.IMPORT, deadLineTimestamp);
        rawRepository.save(raw);
    }

    public void rawExport(Long id) {
        Raws raw = rawRepository.findById(id).orElse(null);

        // 프로그램 시간설정 적용
        Timestamp timestamp = programTimeService.getProgramTime().getCurrentProgramTime();
        Date exportDate = new Date(timestamp.getTime());

        raw.rawExport(exportDate, Status.EXPORT, RawsReason.DISPOSE);
        rawRepository.save(raw);
    }

    /*private RawProductName getRawProductName(String Raw) {
        switch (Raw) {
            case "양배추" :
                return RawProductName.CABBAGE;
            case "흑마늘" :
                return RawProductName.BLACK_GARLIC;
            case "석류(농축액)" :
                return RawProductName.POMEGRANATE;
            case "매실(농축액)" :
                return RawProductName.PLUM;
            case "벌꿀" :
                return RawProductName.HONEY;
            case "콜라겐" :
                return RawProductName.COLLAGEN;
            case "포장지" :
                return RawProductName.WRAPPING_PAPER;
            case "박스" :
                return RawProductName.BOX;
            default:
                return null;
        }
    }*/

    // 이건뭐야 보고 지워야함
    private int calculateRequiredQuantity(double requiredQuantity, Map<String, Integer> stockMap, String product) {
        int currentStock = stockMap.getOrDefault(product, 0);
        return (int) Math.ceil(requiredQuantity - currentStock);
    }


    /* public List<RawOrderPlanDTO> getRawOrderList(Pageable pageable) {

         List<RawOrderPlanDTO> list = new ArrayList<>();
         List<RawShowGraphDTO> rawStockGraph = getRawStockGraph();

         // Initialize stock maps
         Map<String, Integer> ingredient1StockMap = new HashMap<>();
         Map<String, Integer> ingredient2StockMap = new HashMap<>();
         Map<String, Integer> paperStockMap = new HashMap<>();
         Map<String, Integer> boxStockMap = new HashMap<>();

         for (RawShowGraphDTO graphDTO : rawStockGraph) {
             String product = graphDTO.getProduct();
             int quantity = graphDTO.getQuantity();
             if (product.equals("CABBAGE") || product.equals("BLACK_GARLIC") || product.equals("POMEGRANATE") || product.equals("PLUM")) {
                 ingredient1StockMap.put(product, quantity);
             } else if (product.equals("HONEY") || product.equals("COLLAGEN")) {
                 ingredient2StockMap.put(product, quantity);
             } else if (product.equals("WRAPPING_PAPER")) {
                 paperStockMap.put(product, quantity);
             } else if (product.equals("BOX")) {
                 boxStockMap.put(product, quantity);
             }
         }

         Page<Raws> pages = rawRepository.findAll(pageable);


         pages.forEach((e) -> {

             // Create Order to get RawBOMDTO

             RawBOMDTO rawBOMDTO = bomCalculatorService.createRequirement(        Order.builder()
                    // .product(e.getProduct()).quantity(e.getQuantity()).build();

             // Calculate required quantities based on BOM and current stock
             int requiredIngredient1 = calculateRequiredQuantity(rawBOMDTO.getIngredient1(), ingredient1StockMap, e.getProduct().getValue());
             int requiredIngredient2 = calculateRequiredQuantity(rawBOMDTO.getIngredient2(), ingredient2StockMap, e.getProduct().getValue());
             int requiredPaper = calculateRequiredQuantity(rawBOMDTO.getPaper(), paperStockMap, "WRAPPING_PAPER");
             int requiredBox = calculateRequiredQuantity(rawBOMDTO.getBox(), boxStockMap, "BOX");

             list.add(RawOrderPlanDTO.builder()
                     .partner(e.getPartner())
                     .product(e.getProduct().getValue())
                     //.quantity(orederQuantity)
                     .expectedImportDate(e.getExpectedImportDate())
                     .build());
         });

         return list;
     }
 */
    // 재고현황 테이블
    public List<RawsListDTO> getRawStockList(Pageable pageable) {

        List<RawsListDTO> list = new ArrayList<>();

        Page<Raws> pages = rawRepository.findAll(pageable);

        pages.forEach((e) -> {

            // 입고대기 - 주문일, 입고 - 입고일 , 출고 - 출고일 날짜 선택
            Date selectDate;
            switch (e.getStatus()) {
                case WAITING:
                    //selectDate = e.getOrderDate();
                    selectDate = new Date(e.getOrderDate().getTime());
                    break;
                case IMPORT:
                    selectDate = e.getImportDate();
                    break;
                case EXPORT:
                    selectDate = e.getExportDate();
                    break;
                default:
                    //selectDate = e.getOrderDate();
                    selectDate = new Date(e.getOrderDate().getTime());
            }

            list.add(RawsListDTO.builder()
                    .rawsCode(e.getRawsCode())
                    .status(e.getStatus().getValue())
                    .product(e.getProduct().getValue())
                    .Date(selectDate)
                    .quantity(e.getQuantity())
                    .rawsReason(e.getRawsReason() != null ? e.getRawsReason().getValue() : "")
                    .build());
        });
        return list;
    }

    // 입고된 총 양만 list에 담아서 보여주기
    public List<RawShowGraphDTO> getRawStockGraph() {

        Map<String, Integer> productQuantityMap = new HashMap<>();
        for (RawProductName productName : RawProductName.values()) {
            productQuantityMap.put(productName.getValue(), 0); // 기본값은 0으로 설정
        }
        List<Raws> rawsList = rawRepository.findAll();

        for (Raws raws : rawsList) {
            if (raws.getStatus() == Status.IMPORT) {
                String product = raws.getProduct().getValue();
                int quantity = raws.getQuantity();
                productQuantityMap.put(product, productQuantityMap.get(product) + quantity);
            }
        }

        List<RawShowGraphDTO> list = new ArrayList<>();
        productQuantityMap.forEach((product, quantity) -> {
            list.add(new RawShowGraphDTO(product, quantity));
        });

        return list;
    }

    // 재고현황에서 엑설 파일로 다운로드
    @Transactional
    public Workbook getHistory() {
        List<Raws> list = rawRepository.findAll();
        Workbook workbook = new XSSFWorkbook();

        // Create a sheet with a name
        Sheet sheet = workbook.createSheet(LocalDate.now() + " 주문 내역");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = new String[]{"원자재제품코드", "원자재명", "상태(입고,출고,입고대기)", "주문일자", "입고일자", "출고일자"
                , "수량", "사유"};

        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // Fill data rows
        for (int i = 1; i < list.size() + 1; i++) {
            Row row = sheet.createRow(i);
            Raws data = list.get(i - 1);
            //Raws raws = rawOrderInsertRepository.findByRawsCode(data.getRawsCode()).orElse(null);
            row.createCell(0).setCellValue(data.getRawsCode());
            row.createCell(1).setCellValue(data.getProduct().getValue());
            row.createCell(2).setCellValue(data.getStatus().getValue());
            row.createCell(3).setCellValue(data.getOrderDate());
            row.createCell(4).setCellValue(data.getImportDate());
            row.createCell(5).setCellValue(data.getExportDate());
            row.createCell(6).setCellValue(data.getQuantity());
            if (data.getRawsReason() != null) {
                row.createCell(7).setCellValue(data.getRawsReason().getValue());
            } else {
                row.createCell(7).setCellValue("");
            }
        }
        return workbook;
    }

    // 최소 원자재 보유량 알림
    public List<String> checkMinimumStock() {
        List<String> notification = new ArrayList<>();

        List<Raws> rawsList = rawRepository.findAll();

        Map<RawProductName, Integer> stockMap = new HashMap<>();
        for (RawProductName productName : RawProductName.values()) {
            stockMap.put(productName, 0); // 수량없을때 초기값 0설정
        }

        for (Raws raws : rawsList) {
            if (raws.getStatus() == Status.IMPORT) {
                RawProductName product = raws.getProduct();
                int quantity = raws.getQuantity();
                stockMap.put(product, stockMap.get(product) + quantity);
            }
        }

        stockMap.forEach((product, quantity) -> {
            switch (product) {
                case CABBAGE, BLACK_GARLIC:
                    if (quantity <= 1000) {
                        notification.add(product.getValue() + " 의 재고가 1000kg 이하입니다. 현재 수량은 " + quantity + " kg");
                    }
                    break;
                case POMEGRANATE, PLUM, HONEY, COLLAGEN:
                    if (quantity <= 100) {
                        notification.add(product.getValue() + " 의 재고가 100L 이하입니다. 현재 수량은 " + quantity + " L");
                    }
                    break;
                case WRAPPING_PAPER:
                    if (quantity <= 50000) {
                        notification.add(product.getValue() + " 의 재고가 50000개 이하입니다. 현재 수량은 " + quantity + " 개");
                    }
                    break;
                case BOX:
                    if (quantity <= 5000) {
                        notification.add(product.getValue() + " 의 재고가 5000개 이하입니다. 현재 수량은 " + quantity + " 개");
                    }
                    break;
                default:
                    break;
            }
        });
        return notification;
    }

    /*public List<RawPeriodDTO> getPeriodList(Pageable pageable) {

        LocalDate currentDate = LocalDate.now();
        LocalDate minDate = currentDate.minus(ChronoUnit.DAYS.between(currentDate, currentDate.minusDays(3)), ChronoUnit.DAYS);
        LocalDate maxDate = currentDate.plusDays(3);

        Page<Raws> pages = rawRepository.findByStatusAndDeadlineBetween(
                Status.IMPORT,
                java.sql.Date.valueOf(minDate),
                java.sql.Date.valueOf(maxDate),
                pageable);

        List<RawPeriodDTO> list = new ArrayList<>();

        //Page<Raws> pages = rawRepository.findAll(pageable);

        //LocalDate currentDate = LocalDate.now();

        pages.forEach((e) -> {
            /*if (e.getStatus() == Status.IMPORT && e.getDeadLine() != null) {
                LocalDate deadlineDate = e.getDeadLine().toLocalDate();
                long daysUntilDeadline = ChronoUnit.DAYS.between(deadlineDate, currentDate);

                    if ( daysUntilDeadline <=3) {
                        list.add(RawPeriodDTO.builder()
                                .rawsCode(e.getRawsCode())
                                .product(e.getProduct().getValue())
                                .importDate(e.getImportDate())
                                .deadLine(e.getDeadLine())
                                .quantity(e.getQuantity())
                                .build());
        });
        return list;
    } */


    //첫번쨰 생성이 되면
    //분류
    public RawBOMDTO createRequirement(Order order) {
        ProductName productName = order.getProduct();
        switch (productName) {
            case BLACK_GARLIC_JUICE -> {
                return garlicJuiceBOMCal(order.getQuantity());
            }
            case CABBAGE_JUICE -> {
                return cabbageJuiceBOMCal(order.getQuantity());
            }
            case POMEGRANATE_JELLY_STICK -> {
                return pomegranateStickBOMCal(order.getQuantity());
            }
            case PLUM_JELLY_STICK -> {
                return plumStickBOMCal(order.getQuantity());
            }
            //커스텀 Exception 반드시 구현
            default -> throw new RuntimeException();
        }

    }

    public RawBOMDTO garlicJuiceBOMCal(int quantity) {
        double quantity1 = Math.ceil(Math.ceil(Math.ceil(quantity * 30 / 0.97) * 133.33) / 1000);
        double garlic = Math.ceil(quantity1);

        double quantity2 = Math.ceil(quantity * 30 / 0.97) * 5 / 1000;
        double honey = Math.ceil(quantity2);

        double paper = Math.ceil(quantity * 30 / 0.97);
        double box = Math.ceil(quantity / 0.97);

        //return new RawBOMDTO(garlic, honey, paper, box);
        return new RawBOMDTO(0, garlic, 0, 0, honey, 0, paper, box);
    }

    public RawBOMDTO cabbageJuiceBOMCal(int quantity) {
        double quantity1 = Math.ceil(Math.ceil(Math.ceil(quantity * 30 / 0.97) * 133.33) / 1000);
        double cabbage = Math.ceil(quantity1);

        double quantity2 = Math.ceil(quantity * 30 / 0.97) * 5 / 1000;
        double honey = Math.ceil(quantity2);

        double paper = Math.ceil(quantity * 30 / 0.97);
        double box = Math.ceil(quantity / 0.97);

        //return new RawBOMDTO(cabbage, honey, paper, box);
        return new RawBOMDTO(cabbage, 0, 0, 0, honey, 0, paper, box);
    }

    public RawBOMDTO pomegranateStickBOMCal(int quantity) {
        double quantity1 = Math.ceil(Math.ceil(Math.ceil(quantity * 25 / 0.97) * 5) / 1000);
        double pomegranate = Math.ceil(quantity1);

        double quantity2 = Math.ceil(quantity * 25 / 0.97) * 2 / 1000;
        double collagen = Math.ceil(quantity2);

        double paper = Math.ceil(quantity * 25 / 0.97);
        double box = Math.ceil(quantity / 0.97);

        //return new RawBOMDTO(pomegranate, collagen, paper, box);
        return new RawBOMDTO(0, 0, pomegranate, 0, 0, collagen, paper, box);
    }

    public RawBOMDTO plumStickBOMCal(int quantity) {
        double quantity1 = Math.ceil(Math.ceil(Math.ceil(quantity * 25 / 0.97) * 5) / 1000);
        double plum = Math.ceil(quantity1);

        double quantity2 = Math.ceil(quantity * 25 / 0.97) * 2 / 1000;
        double collagen = Math.ceil(quantity2);

        double paper = Math.ceil(quantity * 25 / 0.97);
        double box = Math.ceil(quantity / 0.97);

        //return new RawBOMDTO(plum, collagen, paper, box);
        return new RawBOMDTO(0, 0, 0, plum, 0, collagen, paper, box);
    }


    // 각 주문에 대한 BOM 계산 및 결과 리스트 반환
    public RawBOMDTO calculateBOMs() {
        List<Order> orders = orderRepository.findAll();
        RawBOMDTO totalBOM = new RawBOMDTO();

        for (Order order : orders) {
            RawBOMDTO bomDTO = createRequirement(order);

            totalBOM.setCabbage(totalBOM.getCabbage() + bomDTO.getCabbage());
            totalBOM.setGarlic(totalBOM.getGarlic() + bomDTO.getGarlic());
            totalBOM.setPomegranate(totalBOM.getPomegranate() + bomDTO.getPomegranate());
            totalBOM.setPlum(totalBOM.getPlum() + bomDTO.getPlum());
            totalBOM.setHoney(totalBOM.getHoney() + bomDTO.getHoney());
            totalBOM.setPaper(totalBOM.getPaper() + bomDTO.getPaper());
            totalBOM.setBox(totalBOM.getBox() + bomDTO.getBox());
        }
        return totalBOM;
    }


    public List<RawOrderPlanDTO> getMinus() {
        List<RawShowGraphDTO> list = getRawStockGraph();
        RawBOMDTO bomTotal = calculateBOMs();
        List<RawOrderPlanDTO> result = new ArrayList<>();

        for (RawShowGraphDTO rawShowGraphDTO : list) {
            String product = rawShowGraphDTO.getProduct();
            int stockQuantity = rawShowGraphDTO.getQuantity();

            switch (product) {
                case "양배추":
                    bomTotal.setCabbage(bomTotal.getCabbage() - stockQuantity);
                    result.add(new RawOrderPlanDTO("PartnerName", RawProductName.CABBAGE, bomTotal.getCabbage(), Timestamp.valueOf(LocalDateTime.now())));
                    break;
                case "흑마늘":
                    bomTotal.setGarlic(bomTotal.getGarlic() - stockQuantity);
                    result.add(new RawOrderPlanDTO("PartnerName", RawProductName.BLACK_GARLIC, bomTotal.getGarlic(), Timestamp.valueOf(LocalDateTime.now())));
                    break;
                case "석류(농축액)":
                    bomTotal.setPomegranate(bomTotal.getPomegranate() - stockQuantity);
                    result.add(new RawOrderPlanDTO("PartnerName", RawProductName.POMEGRANATE, bomTotal.getPomegranate(), Timestamp.valueOf(LocalDateTime.now())));
                    break;
                case "매실(농축액)":
                    bomTotal.setPlum(bomTotal.getPlum() - stockQuantity);
                    result.add(new RawOrderPlanDTO("PartnerName", RawProductName.PLUM, bomTotal.getPlum(), Timestamp.valueOf(LocalDateTime.now())));
                    break;
                case "벌꿀":
                    bomTotal.setHoney(bomTotal.getHoney() - stockQuantity);
                    result.add(new RawOrderPlanDTO("PartnerName", RawProductName.HONEY, bomTotal.getHoney(), Timestamp.valueOf(LocalDateTime.now())));
                    break;
                case "콜라겐":
                    bomTotal.setCollagen(bomTotal.getCollagen() - stockQuantity);
                    result.add(new RawOrderPlanDTO("PartnerName", RawProductName.COLLAGEN, bomTotal.getCollagen(), Timestamp.valueOf(LocalDateTime.now())));
                    break;
                case "포장지":
                    bomTotal.setPaper(bomTotal.getPaper() - stockQuantity);
                    result.add(new RawOrderPlanDTO("PartnerName", RawProductName.WRAPPING_PAPER, bomTotal.getPaper(), Timestamp.valueOf(LocalDateTime.now())));
                    break;
                case "박스":
                    bomTotal.setBox(bomTotal.getBox() - stockQuantity);
                    result.add(new RawOrderPlanDTO("PartnerName", RawProductName.BOX, bomTotal.getBox(), Timestamp.valueOf(LocalDateTime.now())));
                    break;
                default:
                    throw new IllegalArgumentException("상품이 없음 : " + product);
            }
        }
        return result;
    }

}
