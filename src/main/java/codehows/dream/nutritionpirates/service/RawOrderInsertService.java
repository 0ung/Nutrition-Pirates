package codehows.dream.nutritionpirates.service;


import codehows.dream.nutritionpirates.constants.RawProductName;
import codehows.dream.nutritionpirates.constants.Status;
import codehows.dream.nutritionpirates.dto.RawOrderInsertDTO;
import codehows.dream.nutritionpirates.dto.RawOrderPlanDTO;
import codehows.dream.nutritionpirates.dto.RawsListDTO;
import codehows.dream.nutritionpirates.entity.Raws;
import codehows.dream.nutritionpirates.repository.RawRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor

public class RawOrderInsertService {

    private final RawRepository rawRepository;

    public void insert(RawOrderInsertDTO rawOrderInsertDTO) {

        validateQuantity(rawOrderInsertDTO);
        Date date = Date.valueOf(LocalDate.now());

        String rawsCode = createRawsCodes(rawOrderInsertDTO);
        Raws raws = Raws.builder()
                .product(rawOrderInsertDTO.getProduct())
                .rawsCode(rawsCode)
                .quantity(rawOrderInsertDTO.getQuantity())
                .partner(rawOrderInsertDTO.getPartner())
                .orderDate(date)
                .status(Status.WAITING)
                .build();

        rawRepository.save(raws);
    }

    private String getRaws(RawProductName productName){
        String code;
        switch (productName) {
            case CABBAGE :
                code = "C";
                break;
            case BLACK_GARLIC :
                code = "B";
                break;
            case POMEGRANATE :
                code = "S";
                break;
            case PLUM :
                code = "P";
                break;
            case HONEY :
                code = "H";
                break;
            case COLLAGEN :
                code = "L";
                break;
            case WRAPPING_PAPER :
                code = "P";
                break;
            case BOX :
                code = "B";
                break;
            default:
                throw new IllegalArgumentException("없는 상품원자재코드 : " + productName);
        }
        return code;
    }
    private String createRawsCodes(RawOrderInsertDTO rawOrderInsertDTO) {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = now.format(formatter);

        int quantity = rawOrderInsertDTO.getQuantity();
        String rawCode = getRaws(rawOrderInsertDTO.getProduct());

        String rawsCode = formattedDate + quantity + rawCode;

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
    public void rawImport (String rawsCode) {

        Raws raw = rawRepository.findByRawsCode(rawsCode).orElse(null);
        raw.rawImport();
        rawRepository.save(raw);
    }

    public void rawExport(String rawsCode) {
        Raws raw = rawRepository.findByRawsCode(rawsCode).orElse(null);
        raw.rawExport();
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





    public List<RawOrderPlanDTO> getRawOrderList(Pageable pageable) {

        List<RawOrderPlanDTO> list = new ArrayList<>();

        Page<Raws> pages = rawRepository.findAll(pageable);

        pages.forEach((e) -> {

            list.add(RawOrderPlanDTO.builder()
                    .partner(e.getPartner())
                    .product(e.getProduct().getValue())
                    .quantity(e.getQuantity())
                    .expectedImportDate(e.getExpectedImportDate())
                    .build());
        });

        return list;
    }

    public List<RawsListDTO> getRawStockList(Pageable pageable) {

        List<RawsListDTO> list = new ArrayList<>();

        Page<Raws> pages = rawRepository.findAll(pageable);

        pages.forEach((e) -> {

            // 입고대기 - 주문일, 입고 - 입고일 , 출고 - 출고일 날짜 선택
            Date selectDate;
            switch (e.getStatus()) {
                case WAITING:
                    selectDate = e.getOrderDate();
                    break;
                case IMPORT:
                    selectDate = e.getImportDate();
                    break;
                case EXPORT:
                    selectDate = e.getExportDate();
                    break;
                default:
                    selectDate = e.getOrderDate();
            }

            list.add(RawsListDTO.builder()
                    .rawsCode(e.getRawsCode())
                    .status(e.getStatus().getValue())
                    .product(e.getProduct().getValue())
                    .Date(selectDate)
                    .quantity(e.getQuantity())
                    .rawsReason(e.getRawsReason()!= null ? e.getRawsReason().getValue() : "")
                    .build());
        });
        return list;
    }

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
            if(data.getRawsReason() != null) {
                row.createCell(7).setCellValue(data.getRawsReason().getValue());
            } else {
                row.createCell(7).setCellValue("");
            }
        }
        return workbook;
    }
}
