package codehows.dream.nutritionpirates;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import codehows.dream.nutritionpirates.constants.ProductName;
import codehows.dream.nutritionpirates.dto.MesOrderInsertDTO;
import codehows.dream.nutritionpirates.service.OrderService;

@WebMvcTest
@TestPropertySource(locations = "classpath:application_test.yml")
public class OrderControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@MockBean
	private OrderService orderService;

	@BeforeEach
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

	}

	@DisplayName("수주 데이터 입력")
	@Test
	void testInsertOrder() throws Exception {
		MesOrderInsertDTO mesOrderInsertDTO = new MesOrderInsertDTO();
		mesOrderInsertDTO.setOrderName("ABC상사");
		mesOrderInsertDTO.setOrderNumber("010-7574-3839");
		mesOrderInsertDTO.setProduct(ProductName.BLACK_GARLIC_JUICE);
		mesOrderInsertDTO.setQuantity(100);
		mesOrderInsertDTO.setIndividual(true);
		mesOrderInsertDTO.setUrgency(false);

		mockMvc.perform(post("/order")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"orderName\":\"ABC상사\",\"orderNumber\":\"010-7574-3839\",\"product\":\"BLACK_GARLIC_JUICE\",\"quantity\":100,\"individual\":true,\"urgency\":false}"))
				.andExpect(status().isCreated());
	}

	@DisplayName("수주 데이터 엑셀 입력")
	@Test
	void testReadExcel() throws Exception {
		File file = new File("src/test/resources/testdata.xlsx");
		FileInputStream fileInputStream = new FileInputStream(file);
		MockMultipartFile mockMultipartFile = new MockMultipartFile("excel", file.getName(), MediaType.APPLICATION_OCTET_STREAM_VALUE, fileInputStream);

		mockMvc.perform(multipart("/order/excel")
						.file(mockMultipartFile))
				.andExpect(status().isCreated());
	}

	@DisplayName("수주 데이터 페이지네이션")
	@Test
	void testGetList() throws Exception {
		mockMvc.perform(get("/order/0"))
				.andExpect(status().isOk());
	}

	@DisplayName("수주 취소")
	@Test
	void testCancelOrder() throws Exception {
		mockMvc.perform(delete("/order/1"))
				.andExpect(status().isOk());
	}

	@DisplayName("발주처 조회")

	@Test
	void testGetOrderer() throws Exception {
		mockMvc.perform(get("/order/orderer"))
				.andExpect(status().isOk());
	}

	@DisplayName("엑셀 다운로드")
	@Test
	void testGetExcel() throws Exception {
		when(orderService.getHistory()).thenReturn(new XSSFWorkbook());

		mockMvc.perform(get("/order/history"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "attachment; filename*=UTF-8''%EC%88%98%EC%A3%BC%20%EB%82%B4%EC%97%AD.xlsx"));
	}

}
