package codehows.dream.nutritionpirates.service;


import codehows.dream.nutritionpirates.constants.RawProductName;
import codehows.dream.nutritionpirates.constants.ProductName;
import codehows.dream.nutritionpirates.constants.RawsReason;
import codehows.dream.nutritionpirates.constants.Status;
import codehows.dream.nutritionpirates.dto.*;
import codehows.dream.nutritionpirates.entity.Order;
import codehows.dream.nutritionpirates.entity.Raws;
import codehows.dream.nutritionpirates.repository.OrderRepository;
import codehows.dream.nutritionpirates.repository.RawRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Log4j2
public class RawOrderInsertService {

    private final RawRepository rawRepository;
    private final OrderRepository orderRepository;
    private final ProgramTimeService programTimeService;


    // 영업일 및 공휴일을 고려하여 예상 입고일 계산
    private LocalDate addBusinessDaysSwitch(LocalDate startDate, int businessDays) {
        LocalDate date = startDate;
        int daysAdded = 0;

        while (daysAdded < businessDays) {
            date = date.plusDays(1);
            if (isBusinessDay(date)) {
                daysAdded++;
            }
        }
        return date;
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
                LocalDate.of(date.getYear(), 2, 10), // 설날
                LocalDate.of(date.getYear(), 3, 1), // 삼일절
                LocalDate.of(date.getYear(), 5, 5), // 어린이날
                LocalDate.of(date.getYear(), 5, 15), // 부처님오시는날
                LocalDate.of(date.getYear(), 6, 6), // 현출일
                LocalDate.of(date.getYear(), 8, 15), // 광복절
                LocalDate.of(date.getYear(), 9, 16), // 추석연휴
                LocalDate.of(date.getYear(), 9, 17), // 추석연휴
                LocalDate.of(date.getYear(), 9, 18), // 추석연휴
                LocalDate.of(date.getYear(), 10, 3), // 개천절
                LocalDate.of(date.getYear(), 10, 9), // 한글날
                LocalDate.of(date.getYear(), 12, 25) // 크리스마스
                // 추가 공휴일을 여기에 추가
        );

        if (holidays.contains(date)) {
            return false;
        }
        return true;
    }


    private Timestamp calculateExpectedImportDate(Timestamp orderDateTime, RawProductName product) {
        LocalDateTime orderDateTimeLocal = orderDateTime.toLocalDateTime();
        LocalTime time = orderDateTimeLocal.toLocalTime();
        DayOfWeek dayOfWeek = orderDateTimeLocal.getDayOfWeek();
        int daysToAdd;

        switch (product) {
            case CABBAGE, BLACK_GARLIC, HONEY:
                if (time.isBefore(LocalTime.NOON)) {
                    daysToAdd = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) ? 3 : 2;
                } else {
                    daysToAdd = 3;
                }
                break;
            case PLUM, POMEGRANATE, COLLAGEN:
                if (time.isBefore(LocalTime.of(15, 0))) {
                    daysToAdd = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) ? 4 : 3;
                } else {
                    daysToAdd = 4;
                }
                break;
            case WRAPPING_PAPER:
                daysToAdd = 7;
                break;
            case BOX:
                daysToAdd = 3;
                break;
            default:
                throw new IllegalArgumentException("Unknown product: " + product);
        }

        LocalDate date = orderDateTimeLocal.toLocalDate();
        // 날짜 계산을 위한 daysToAdd 더하기
        date = addBusinessDaysSwitch(date, daysToAdd);
        // 최종 계산된 LocalDate와 원래의 LocalTime을 결합하여 LocalDateTime을 만듦
        LocalDateTime expectedDateTime = date.atTime(time);
        // LocalDate를 Timestamp로 변환
        return Timestamp.valueOf(expectedDateTime);
    }

    public void insert(RawOrderInsertDTO rawOrderInsertDTO) {
        validateQuantity(rawOrderInsertDTO);

        Timestamp timestamp = programTimeService.getProgramTime().getCurrentProgramTime();

        // calculateExpectedImportDate 메서드 호출
        Timestamp expectedImportDateTimestamp = calculateExpectedImportDate(timestamp, rawOrderInsertDTO.getProduct());


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

    public void rawImport(String rawsCode) {

        Raws raw = rawRepository.findByRawsCode(rawsCode).orElse(null);

        // 프로그램 시간설정 적용
        Timestamp timestamp = programTimeService.getProgramTime().getCurrentProgramTime();
        //Date importDate = new Date(timestamp.getTime());

        // deadLine 계산
        LocalDateTime deadLineDateTime = timestamp.toLocalDateTime().plusDays(14);
        Timestamp deadLineTimestamp = Timestamp.valueOf(deadLineDateTime);

        //importDate 를 Timestamp로 설정
        Timestamp importDate = timestamp;

        raw.rawImport(importDate, Status.IMPORT, deadLineTimestamp);
        rawRepository.save(raw);
    }


    public void rawExport(String rawsCode) {
        Raws raw = rawRepository.findByRawsCode(rawsCode).orElse(null);

        // 프로그램 시간설정 적용
        Timestamp timestamp = programTimeService.getProgramTime().getCurrentProgramTime();
        Date exportDate = new Date(timestamp.getTime());

        raw.rawExport(exportDate, Status.EXPORT, RawsReason.DISPOSE);
        rawRepository.save(raw);
    }

    // 발주등록이후 테이블
    public List<RawOrderListDTO> getRawOrderList(Pageable pageable) {

        List<RawOrderListDTO> list = new ArrayList<>();

        Page<Raws> pages = rawRepository.findAll(pageable);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        pages.forEach((e) -> {

            // status가 export 인지 확인하고 먼저 걸러내기
            if (e.getStatus() != Status.EXPORT) {
                String formattedorder = null;
                String formattedimport = null;
                if (e.getOrderDate() != null) {
                    Timestamp timestamp = e.getOrderDate();
                    LocalDateTime localDateTime = timestamp.toLocalDateTime();
                    formattedorder = localDateTime.format(formatter);
                }
                if (e.getImportDate() != null) {
                    Timestamp timestamp = e.getImportDate();
                    LocalDateTime localDateTime = timestamp.toLocalDateTime();
                    formattedimport = localDateTime.format(formatter);
                }

                list.add(RawOrderListDTO.builder()
                        .rawsCode(e.getRawsCode())
                        .product(e.getProduct().getValue())
                        .quantity(e.getQuantity())
                        .status(e.getStatus().getValue())
                        .orderDate(formattedorder)
                        .importDate(formattedimport)
                        .build()
                );
            }
        });
        return list;
    }

    // 발주현황에서 엑설 파일로 다운로드
    @Transactional
    public Workbook getHistoryRaw() {

        // Pageable을 사용하지 않고 모든 데이터를 가져오기 위해 임의의 Pageable 생성
        Pageable pageable = Pageable.unpaged();

        // getRawOrderList메서드로 호출하여 RawOrderListDTO 리스트 가져오기
        List<RawOrderListDTO> list = getRawOrderList(pageable);

        Workbook workbook = new XSSFWorkbook();

        // 엑셀 아래 시트 이름 설정
        Sheet sheet = workbook.createSheet(LocalDate.now() + " 원자재 입고 내역");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = new String[]{"원자재제품코드", "원자재명", "수량", "입고상태", "주문일자", "입고일자"};

        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // Fill data rows
        for (int i = 1; i < list.size() + 1; i++) {
            Row row = sheet.createRow(i);
            RawOrderListDTO data = list.get(i - 1);

            row.createCell(0).setCellValue(data.getRawsCode());
            row.createCell(1).setCellValue(data.getProduct());
            row.createCell(2).setCellValue(data.getQuantity());
            row.createCell(3).setCellValue(data.getStatus());
            row.createCell(4).setCellValue(data.getOrderDate());
            row.createCell(5).setCellValue(data.getImportDate());
        }
        return workbook;
    }


    // 재고현황 테이블

    public Page<RawsListDTO> getRawStockList(Pageable pageable) {
        // 페이지네이션된 원자재 리스트 조회
        Page<Raws> pages = rawRepository.findAll(pageable);

        // 각 원자재의 상태에 따라 DTO로 변환하여 리스트 생성
        List<RawsListDTO> list = pages.stream().map(e -> {
            // 상태에 따라 적절한 날짜 선택
            Date selectDate;
            switch (e.getStatus()) {
                case WAITING:
                    selectDate = new Date(e.getOrderDate().getTime());  // 주문일 선택
                    break;
                case IMPORT:
                    selectDate = new Date(e.getImportDate().getTime()); // 입고일 선택
                    break;
                case EXPORT:
                    selectDate = e.getExportDate();                     // 출고일 선택
                    break;
                default:
                    selectDate = new Date(e.getOrderDate().getTime());  // 기본적으로 주문일 선택
            }

            // RawsListDTO 객체 생성
            return RawsListDTO.builder()
                    .rawsCode(e.getRawsCode())
                    .status(e.getStatus().getValue())
                    .product(e.getProduct().getValue())
                    .Date(selectDate)
                    .quantity(e.getQuantity())
                    .rawsReason(e.getRawsReason() != null ? e.getRawsReason().getValue() : "")
                    .build();
        }).collect(Collectors.toList());

        // Page 객체로 변환하여 반환
        return new PageImpl<>(list, pageable, pages.getTotalElements());
    }

    // 재고현황 테이블
    /*public Page<RawsListDTO> getRawStockList(Pageable pageable) {
        Page<Raws> pages = rawRepository.findAll(pageable);

        List<RawsListDTO> list = pages.stream().map(e -> {
            // 입고대기 - 주문일, 입고 - 입고일 , 출고 - 출고일 날짜 선택
            Date selectDate;
            switch (e.getStatus()) {
                case WAITING:
                    selectDate = new Date(e.getOrderDate().getTime());
                    break;
                case IMPORT:
                    selectDate = new Date(e.getImportDate().getTime());
                    break;
                case EXPORT:
                    selectDate = e.getExportDate();
                    break;
                default:
                    selectDate = new Date(e.getOrderDate().getTime());
            }

            return RawsListDTO.builder()
                    .rawsCode(e.getRawsCode())
                    .status(e.getStatus().getValue())
                    .product(e.getProduct().getValue())
                    .Date(selectDate)
                    .quantity(e.getQuantity())
                    .rawsReason(e.getRawsReason() != null ? e.getRawsReason().getValue() : "")
                    .build();
        }).collect(Collectors.toList());

        // Page 객체를 반환하기 위해, List를 Page로 변환
        return new PageImpl<>(list, pageable, pages.getTotalElements());
    }*/

    // 재고현황 테이블
    /*public List<RawsListDTO> getRawStockList(Pageable pageable) {

        List<RawsListDTO> list = new ArrayList<>();

        Page<Raws> pages = rawRepository.findAll(pageable);
        int totalPages = pages.getTotalPages();

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

        // 원하는 위치에 전체 페이지 수를 사용
        System.out.println("Total pages: " + totalPages);

        return list;
    }*/


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

    // 원자재현황에서 엑설 파일로 다운로드
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

    // 기간별 원자재 리스트 페이징 기능
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

    /*public List<RawPeriodDTO> getPeriodList(Pageable pageable) {

        Timestamp timestamp = programTimeService.getProgramTime().getCurrentProgramTime();
        LocalDateTime currentDate = timestamp.toLocalDateTime();

        // deadLine에서 3일내 시간범위 계산
        LocalDateTime minDate = currentDate.minusDays(3);
        Timestamp minTimestamp = Timestamp.valueOf(minDate);

        // deadLine 이 현재시간 에서 현재시간 -3일 Data만 들고옴
        Page<Raws> pages = rawRepository.findByStatusAndDeadlineBetween(
                Status.IMPORT,
                minTimestamp,
                timestamp,
                pageable);

        List<RawPeriodDTO> list = new ArrayList<>();

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
    }*/

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

        return new RawBOMDTO(0, garlic, 0, 0, honey, 0, paper, box);
    }

    public RawBOMDTO cabbageJuiceBOMCal(int quantity) {
        double quantity1 = Math.ceil(Math.ceil(Math.ceil(quantity * 30 / 0.97) * 133.33) / 1000);
        double cabbage = Math.ceil(quantity1);

        double quantity2 = Math.ceil(quantity * 30 / 0.97) * 5 / 1000;
        double honey = Math.ceil(quantity2);

        double paper = Math.ceil(quantity * 30 / 0.97);
        double box = Math.ceil(quantity / 0.97);

        return new RawBOMDTO(cabbage, 0, 0, 0, honey, 0, paper, box);
    }

    public RawBOMDTO pomegranateStickBOMCal(int quantity) {
        double quantity1 = Math.ceil(Math.ceil(Math.ceil(quantity * 25 / 0.97) * 5) / 1000);
        double pomegranate = Math.ceil(quantity1);

        double quantity2 = Math.ceil(quantity * 25 / 0.97) * 2 / 1000;
        double collagen = Math.ceil(quantity2);

        double paper = Math.ceil(quantity * 25 / 0.97);
        double box = Math.ceil(quantity / 0.97);

        return new RawBOMDTO(0, 0, pomegranate, 0, 0, collagen, paper, box);
    }

    public RawBOMDTO plumStickBOMCal(int quantity) {
        double quantity1 = Math.ceil(Math.ceil(Math.ceil(quantity * 25 / 0.97) * 5) / 1000);
        double plum = Math.ceil(quantity1);

        double quantity2 = Math.ceil(quantity * 25 / 0.97) * 2 / 1000;
        double collagen = Math.ceil(quantity2);

        double paper = Math.ceil(quantity * 25 / 0.97);
        double box = Math.ceil(quantity / 0.97);

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
            totalBOM.setCollagen(totalBOM.getCollagen() + bomDTO.getCollagen());
            totalBOM.setBox(totalBOM.getBox() + bomDTO.getBox());
        }
        return totalBOM;
    }


    public List<RawOrderPlanDTO> getMinus() {
        List<RawShowGraphDTO> list = getRawStockGraph();
        RawBOMDTO bomTotal = calculateBOMs();

        Timestamp timestamp = programTimeService.getProgramTime().getCurrentProgramTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        List<RawOrderPlanDTO> result = new ArrayList<>();

        for (RawShowGraphDTO rawShowGraphDTO : list) {
            String product = rawShowGraphDTO.getProduct();
            int stockQuantity = rawShowGraphDTO.getQuantity();
            String partnerName = "";
            RawProductName rawProductName;
            double remainQuantity;

            switch (product) {
                case "양배추":
                    partnerName = "에이농장";
                    rawProductName = RawProductName.CABBAGE;
                    remainQuantity = bomTotal.getCabbage() - stockQuantity;
                    bomTotal.setCabbage(remainQuantity);
                    break;
                case "흑마늘":
                    partnerName = "에이농장";
                    rawProductName = RawProductName.BLACK_GARLIC;
                    remainQuantity = bomTotal.getGarlic() - stockQuantity;
                    bomTotal.setGarlic(remainQuantity);
                    break;
                case "석류(농축액)":
                    partnerName = "OO농협";
                    rawProductName = RawProductName.POMEGRANATE;
                    remainQuantity = bomTotal.getPomegranate() - stockQuantity;
                    bomTotal.setPomegranate(remainQuantity);
                    break;
                case "매실(농축액)":
                    partnerName = "OO농협";
                    rawProductName = RawProductName.PLUM;
                    remainQuantity = bomTotal.getPlum() - stockQuantity;
                    bomTotal.setPlum(remainQuantity);
                    break;
                case "벌꿀":
                    partnerName = "에이농장";
                    rawProductName = RawProductName.HONEY;
                    remainQuantity = bomTotal.getHoney() - stockQuantity;
                    bomTotal.setHoney(remainQuantity);
                    break;
                case "콜라겐":
                    partnerName = "OO농협";
                    rawProductName = RawProductName.COLLAGEN;
                    remainQuantity = bomTotal.getCollagen() - stockQuantity;
                    bomTotal.setCollagen(remainQuantity);
                    break;
                case "포장지":
                    partnerName = "OO포장";
                    rawProductName = RawProductName.WRAPPING_PAPER;
                    remainQuantity = bomTotal.getPaper() - stockQuantity;
                    bomTotal.setPaper(remainQuantity);
                    break;
                case "박스":
                    partnerName = "OO포장";
                    rawProductName = RawProductName.BOX;
                    remainQuantity = bomTotal.getBox() - stockQuantity;
                    bomTotal.setBox(remainQuantity);
                    break;
                default:
                    throw new IllegalArgumentException("상품이 없음 : " + product);
            }

            if (remainQuantity > 0) {
                Timestamp expectedImportDate = calculateExpectedImportDate(timestamp, rawProductName);
                String formattedExpectedImportDate = expectedImportDate.toLocalDateTime().format(formatter);
                result.add(new RawOrderPlanDTO(partnerName, rawProductName.getValue(), remainQuantity, formattedExpectedImportDate));
            }
        }

        return result;


    }

}