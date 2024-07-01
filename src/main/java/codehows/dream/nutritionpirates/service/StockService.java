package codehows.dream.nutritionpirates.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codehows.dream.nutritionpirates.constants.FacilityStatus;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.constants.ProductName;
import codehows.dream.nutritionpirates.dto.ShipmentListDTO;
import codehows.dream.nutritionpirates.dto.StockGraphDTO;
import codehows.dream.nutritionpirates.dto.StockShowDTO;
import codehows.dream.nutritionpirates.dto.ShipShowGraphDTO;
import codehows.dream.nutritionpirates.entity.Order;
import codehows.dream.nutritionpirates.entity.ProcessPlan;
import codehows.dream.nutritionpirates.entity.Stock;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.repository.OrderRepository;
import codehows.dream.nutritionpirates.repository.ProcessPlanRepository;
import codehows.dream.nutritionpirates.repository.StockRepository;
import codehows.dream.nutritionpirates.repository.WorkPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class StockService {

    private final StockRepository stockRepository;
    private final WorkPlanService workPlanService;
    private final WorkPlanRepository workPlanRepository;
    private final ProgramTimeService programTimeService;
    private final OrderRepository orderRepository;
    private final ProcessPlanRepository processPlanRepository;
    /*public StockShowDTO getStockByWorkPlan(Long id) {
        WorkPlan workPlan = workPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("WorkPlan not found for id: " + id));

        if (workPlan.getFacility() == Facility.boxMachine) {
            return StockShowDTO.builder()

                    .lotCode(workPlan.getLotCode().toString())
                    .quantity(workPlan.getSemiProduct()*0.97)
                    //.createDate(workPlan.getEndTime())
                    .build();
        } else {
            throw new IllegalArgumentException("Facility is not boxMachine for WorkPlan id: " + id);
        }
    }*/

    private String parseRawsCodes(String rawsCodes) {
        if (rawsCodes == null) {
            return null;
        }

        if (rawsCodes.contains("G")) {
            return "흑마늘";
        } else if (rawsCodes.contains("C")) {
            return "양배추";
        } else if (rawsCodes.contains("S")) {
            return "석류";
        } else if (rawsCodes.contains("P")) {
            return "매실";
        }

        return "알 수 없음"; // 정의되지 않은 경우
    }


    // isExport 가 1(true) 이면 출고 0 (false) 이면 입고
    /*public List<StockShowDTO> getStock(Pageable pageable) {
        Page<Stock> stocks = stockRepository.findAll(pageable);
        List<StockShowDTO> stockShowDTOList = new ArrayList<>();

        stocks.forEach((e) -> {
            stockShowDTOList.add(
                    StockShowDTO.builder()
                            .product(e.getWorkPlan().getProcessPlan().getOrder().getProduct().getValue())
                            .lotCode(e.getWorkPlan().getLotCode().getLotCode())
                            .quantity(e.getQuantity())
                            .createDate(e.getCreateDate())
                            .isExport(e.getExportDate() == null ? false : true)
                            .exportDate(e.getExportDate())
                            .build()
            );
        });
        return stockShowDTOList;
    }*/

    public void releaseStock(Long id) {
        Stock stock = stockRepository.findById(id).orElse(null);

        // 프로그램 시간설정 적용
        Timestamp timestamp = programTimeService.getProgramTime().getCurrentProgramTime();
        Date exportDate = new Date(timestamp.getTime());

        stock.updateIsExport(true, exportDate);
        stockRepository.save(stock);
    }

    public List<StockGraphDTO> getGraphStock() {

        Map<String, Integer> stockQuantityMap = new HashMap<>();
        for (ProductName productName : ProductName.values()) {
            stockQuantityMap.put(productName.getValue(), 0); // 기본값으로 설정
        }

        List<Stock> stockList = stockRepository.findAll();

        for (Stock stock : stockList) {
            if (stock.isExport() == false) {
                String product = stock.getWorkPlan().getProcessPlan().getOrder().getProduct().getValue();
                int quantity = stock.getQuantity();
                stockQuantityMap.put(product, stockQuantityMap.get(product) + quantity);
            }
        }
        List<StockGraphDTO> list = new ArrayList<>();
        stockQuantityMap.forEach((product, quantity) -> {
            list.add(new StockGraphDTO(product, quantity));
        });
        return list;
    }

    public List<StockGraphDTO> getGraphStockexport() {
        Map<String, Integer> exportQuantityMap = new HashMap<>();
        for (ProductName productName : ProductName.values()) {
            exportQuantityMap.put(productName.getValue(), 0); // 초기값 설정
        }
        List<Stock> exportList = stockRepository.findAll();

        for (Stock stock : exportList) {
            if (stock.isExport() == true) {
                String product = stock.getWorkPlan().getProcessPlan().getOrder().getProduct().getValue();
                int quantity = stock.getQuantity();
                exportQuantityMap.put(product, exportQuantityMap.get(product) + quantity);
            }
        }
        List<StockGraphDTO> list = new ArrayList<>();
        exportQuantityMap.forEach((product, quantity) -> {
            list.add(new StockGraphDTO(product, quantity));
        });
        return list;
    }

    /*// 출하 DTO 만들기
    public List<ShipmentListDTO> getShip(Pageable pageable) {
        //List<Order> orders2 = orderRepository.findAll();
        Page<Order> orders = orderRepository.findAll(pageable);

        List<ShipmentListDTO> shipmentListDTOList = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        orders.forEach((e) -> {
            // Format expectedDeliveryDate if it's not null
            String formattedDate = null;
            if (e.getExpectedDeliveryDate() != null) {
                try {
                    LocalDateTime localDateTime = LocalDateTime.parse(e.getExpectedDeliveryDate());
                    formattedDate = localDateTime.format(formatter);
                } catch (DateTimeParseException ex) {
                    // Handle parsing exception if the format is incorrect
                    log.error("Error parsing expectedDeliveryDate: " + e.getExpectedDeliveryDate(), ex);
                }
            }

            Process process = getProcess(e.getId());
            shipmentListDTOList.add(
                    ShipmentListDTO.builder()
                            .orderName(e.getOrderer().getName())
                            .product(e.getProduct().getValue())
                            .quantity(e.getQuantity())
                            .orderDate(e.getOrderDate())
                            .expectedDeliveryDate(formattedDate) // Assign formattedDate here
                            .process(process)
                            .urgency(e.isUrgency())
                            .build()
            );
        });

        return shipmentListDTOList;
    }*/

    //* process 공정찾는 메서드*/
    public Process getProcess(Long orderId) {
        //1. processPlan 찾고
        //2. workplan을 찾고
        //3. 진행중인 공정을 반환
        ProcessPlan plan = processPlanRepository.findByOrderId(orderId);
        List<WorkPlan> workPlan = workPlanRepository.findAllByProcessPlanId(plan.getId());

        for (WorkPlan workPlan1 : workPlan) {
            if (workPlan1.getFacilityStatus() == FacilityStatus.WORKING) {
                return workPlan1.getProcess();
            }
            if (workPlan1.getFacilityStatus() == FacilityStatus.STANDBY && workPlan1.getProcess() == Process.A8 && workPlan1.getRawsCodes() != null) {
                return workPlan1.getProcess();
            }
        }
        throw new IllegalArgumentException("대기중");
    }

    // 재고현황 엑셀파일 다운로드
    @Transactional
    public Workbook getHistorystock() {

        // Pageable을 사용하지 않고 모든 데이터를 가져오기 위해 임의의 Pageable 생성
        Pageable pageable = Pageable.unpaged();

        // getStock메서드를 호출하여 StockShowDTO 리스트 가져오기
        Page<StockShowDTO> page = getStock(pageable);
        List<StockShowDTO> list = page.getContent();

        Workbook workbook = new XSSFWorkbook();

        // 엑셀 아래 시트 이름 설정
        Sheet sheet = workbook.createSheet(LocalDate.now() + "재고 내역");

        Row headerRow = sheet.createRow(0);
        String[] headers = new String[]{"완제품명", "LOT번호", "수량", "생산 날짜", "출고 날짜", "입/출고 유무"};

        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        for (int i = 1; i < list.size() + 1; i++) {
            Row row = sheet.createRow(i);
            StockShowDTO data = list.get(i - 1);

            row.createCell(0).setCellValue(data.getProduct());
            row.createCell(1).setCellValue(data.getLotCode());
            row.createCell(2).setCellValue(data.getQuantity());
            row.createCell(3).setCellValue(data.getCreateDate());
            row.createCell(4).setCellValue(data.getExportDate());
            row.createCell(5).setCellValue(data.isExport());
        }

        return workbook;
    }

    public Long getTotalPages() {
        long cnt = stockRepository.count();

        if (cnt == 0) {
            throw new RuntimeException();
        }
        long result = cnt / 10;
        long remain = cnt % 10;

        if (remain > 0) {
            return result + 1;
        }
        return result;
    }


    // 출하현황 엑셀파일 다운로드
    @Transactional
    public Workbook getHistroyship() {

        // Pageable을 사용하지 않고 모든 데이터를 가져오기 위해 임의의 Pageable 생성
        Pageable pageable = Pageable.unpaged();

        // getStock메서드를 호출하여 StockShowDTO 리스트 가져오기
        Page<ShipmentListDTO> page = getShip(pageable);
        List<ShipmentListDTO> list = page.getContent();

        Workbook workbook = new XSSFWorkbook();

        // 엑셀 아래 시트 이름 설정
        Sheet sheet = workbook.createSheet(LocalDate.now() + "재고 내역");

        Row headerRow = sheet.createRow(0);
        String[] headers = new String[]{"발주처", "완제품명", "수량", "주문 날짜", "출고 날짜", "예상납기일", "공정상태", "긴급여부", "출하상태"};

        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        for (int i = 1; i < list.size() + 1; i++) {
            Row row = sheet.createRow(i);
            ShipmentListDTO data = list.get(i - 1);

            row.createCell(0).setCellValue(data.getOrderName());
            row.createCell(1).setCellValue(data.getProduct());
            row.createCell(2).setCellValue(data.getQuantity());
            row.createCell(3).setCellValue(data.getOrderDate());
            //row.createCell(4).setCellValue(data.getShippingDate());
            row.createCell(5).setCellValue(data.getExpectedDeliveryDate());
            row.createCell(6).setCellValue(data.getProcess().toString());
            row.createCell(7).setCellValue(data.isUrgency());
            row.createCell(8).setCellValue(data.isShipping());
        }

        return workbook;
    }

    // 재고 현황 테이블 - /api/stock/{page}
    public Page<StockShowDTO> getStock(Pageable pageable) {
        Page<Stock> pages = stockRepository.findAll(pageable);

        List<StockShowDTO> list = pages.stream().map(e ->

                StockShowDTO.builder()
                        .product(e.getWorkPlan().getProcessPlan().getOrder().getProduct().getValue())
                        .lotCode(e.getWorkPlan().getLotCode().getLotCode())
                        .quantity(e.getQuantity())
                        .createDate(e.getCreateDate())
                        .exportDate(e.getExportDate())
                        //.isExport(e.getExportDate() == null ? false : true)
                        .isExport(e.getExportDate() != null)
                        .build()

        ).collect(Collectors.toList());

        // Page 객체를 반환하기 위해 List를 Page 로 변환
        return new PageImpl<>(list, pageable, pages.getTotalElements());

    }

    /**/
    public Page<ShipmentListDTO> getShip(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);

        List<ShipmentListDTO> list = orders.stream().map(e -> {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            String formattedDate = null;

            if (e.getExpectedDeliveryDate() != null) {
                try {
                    LocalDateTime localDateTime = LocalDateTime.parse(e.getExpectedDeliveryDate());
                    formattedDate = localDateTime.format(formatter);
                } catch (DateTimeParseException ex) {
                    // Handle parsing exception if the format is incorrect
                    log.error("Error parsing expectedDeliveryDate: " + e.getExpectedDeliveryDate(), ex);
                }
            }

            Process process = getProcess(e.getId());
            return ShipmentListDTO.builder()
                    .orderName(e.getOrderer().getName())
                    .product(e.getProduct().getValue())
                    .quantity(e.getQuantity())
                    .orderDate(e.getOrderDate())
                    //.exportDate()
                    .expectedDeliveryDate(formattedDate) // Assign formattedDate here
                    .process(process)
                    .urgency(e.isUrgency())
                    .build();

        }).collect(Collectors.toList());

        return new PageImpl<>(list, pageable, orders.getTotalElements());
    }
}
