package codehows.dream.nutritionpirates.service;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import codehows.dream.nutritionpirates.constants.ProductName;
import codehows.dream.nutritionpirates.dto.MesOrderDTO;
import codehows.dream.nutritionpirates.dto.MesOrderInsertDTO;
import codehows.dream.nutritionpirates.entity.Order;
import codehows.dream.nutritionpirates.entity.Orderer;
import codehows.dream.nutritionpirates.repository.OrderRepository;
import codehows.dream.nutritionpirates.repository.OrdererRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class OrderService {

	private final OrderRepository orderRepository;
	private final OrdererRepository ordererRepository;

	public Orderer insertOrderer(String ordererName, String ordererNumber) {
		Orderer orderer = ordererRepository.findByName(ordererName).orElse(null);
		// 발주처가 있을때
		if (orderer != null) {
			return orderer;
		}

		//발주처가 없을떄
		return ordererRepository.save(Orderer.builder()
			.name(ordererName)
			.phoneNumber(ordererNumber)
			.build());
	}

	public void insert(MesOrderInsertDTO mesOrderInsertDTO) {
		Orderer orderer = insertOrderer(mesOrderInsertDTO.getOrderName(), mesOrderInsertDTO.getOrderNumber());

		Date nowDate = Date.valueOf(LocalDate.now());
		//예상 납기일 계산

		orderRepository.save(Order.builder()
			.orderer(orderer)
			.product(mesOrderInsertDTO.getProduct())
			.quantity(mesOrderInsertDTO.getQuantity())
			.individual(mesOrderInsertDTO.isIndividual())
			.urgency(mesOrderInsertDTO.isUrgency())
			.expectedDeliveryDate(String.valueOf(LocalDate.now()))
			.orderDate(nowDate)
			.invisible(false)
			.build());
	}

	private ProductName getProductName(String product) {
		switch (product) {
			case "양배추즙":
				return ProductName.CABBAGE_JUICE;
			case "흑마늘즙":
				return ProductName.BLACK_GARLIC_JUICE;
			case "매실스틱":
				return ProductName.PLUM_JELLY_STICK;
			case "석류스틱":
				return ProductName.POMEGRANATE_JELLY_STICK;
			default:
				return null;
		}
	}

	public void readExcel(MultipartFile excel) {
		try {
			// Directly create Workbook from MultipartFile input stream
			Workbook workbook = new XSSFWorkbook(excel.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);
			for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
				Row row = sheet.getRow(i);
				MesOrderInsertDTO mesOrderInsertDTO = new MesOrderInsertDTO();
				for (int j = 0; j < 6; j++) {
					String cellValue = row.getCell(j).toString();
					switch (j) {
						case 0:
							mesOrderInsertDTO.setOrderName(cellValue);
							break;
						case 1:
							mesOrderInsertDTO.setOrderNumber(cellValue);
							break;
						case 2:
							mesOrderInsertDTO.setProduct(parseProduct(cellValue));
							break;
						case 3:
							mesOrderInsertDTO.setQuantity(parseInt(cellValue));
							break;
						case 4:
							mesOrderInsertDTO.setUrgency(Boolean.parseBoolean(cellValue));
							break;
						case 5:
							mesOrderInsertDTO.setIndividual(Boolean.parseBoolean(cellValue));
							break;
					}
				}
				insert(mesOrderInsertDTO);
			}
		} catch (IOException e) {
			log.error("엑셀 파일 읽기 중 오류 발생: " + e.getMessage());
		}
	}

	private int parseInt(String cellValue) {
		// Remove decimal point if present
		if (cellValue.contains(".")) {
			cellValue = cellValue.substring(0, cellValue.indexOf('.'));
		}
		return Integer.parseInt(cellValue);
	}

	private ProductName parseProduct(String cellValue) {
		return switch (cellValue) {
			case "양배추즙" -> ProductName.CABBAGE_JUICE;
			case "흑마늘즙" -> ProductName.BLACK_GARLIC_JUICE;
			case "매실스틱" -> ProductName.PLUM_JELLY_STICK;
			case "석류스틱" -> ProductName.POMEGRANATE_JELLY_STICK;
			default -> null;
		};
	}

	public List<MesOrderDTO> getOrderList(Pageable pageable) {
		List<MesOrderDTO> list = new ArrayList<>();
		Page<Order> pages = orderRepository.findAll(pageable);

		pages.forEach((e) -> {
			Orderer orderer = ordererRepository.findById(e.getOrderer().getId()).orElse(null);
			list.add(MesOrderDTO.builder()
					.orderId(e.getId())
					.ordererName(orderer.getName())
					.orderDate(e.getOrderDate())
					.expectedDeliveryDate(e.getExpectedDeliveryDate())
					.product(e.getProduct().getValue())
					.quantity(e.getQuantity())
					.urgency(e.isUrgency())
					.visible(e.isInvisible())
					.build());
		});
		return list;
	}

	public void cancelOrder(Long id) {
		Order order = orderRepository.findById(id).orElse(null);
		order.updateInvisible(true);
	}

	public List<Orderer> getOrderer() {
		return ordererRepository.findAll();
	}

	public List<Orderer> getOrderer(Pageable pageable) {
		List<Orderer> list = new ArrayList<>();
		Page<Orderer> pages = ordererRepository.findAll(pageable);

		pages.forEach((e) -> {
			Orderer orderer = ordererRepository.findById(e.getId()).orElse(null);
			list.add(Orderer.builder()
					.id(e.getId())
					.name(orderer.getName())
					.phoneNumber(orderer.getPhoneNumber())
					.build());
		});
		return list;
	}


	public Long getTotalPages(){
		return ordererRepository.count();
	}
}
