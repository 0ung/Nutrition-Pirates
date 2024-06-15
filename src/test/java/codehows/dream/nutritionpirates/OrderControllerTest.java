package codehows.dream.nutritionpirates;

import codehows.dream.nutritionpirates.constants.ProductName;
import codehows.dream.nutritionpirates.controller.OrderController;
import codehows.dream.nutritionpirates.dto.MesOrderInsertDTO;
import codehows.dream.nutritionpirates.entity.Orderer;
import codehows.dream.nutritionpirates.service.OrderService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
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

	@Test
	void testReadExcel() throws Exception {
		File file = new File("src/test/resources/testdata.xlsx");
		FileInputStream fileInputStream = new FileInputStream(file);
		MockMultipartFile mockMultipartFile = new MockMultipartFile("excel", file.getName(), MediaType.APPLICATION_OCTET_STREAM_VALUE, fileInputStream);

		mockMvc.perform(multipart("/order/excel")
						.file(mockMultipartFile))
				.andExpect(status().isCreated());
	}

	@Test
	void testGetList() throws Exception {
		mockMvc.perform(get("/order/0"))
				.andExpect(status().isOk());
	}

	@Test
	void testCancelOrder() throws Exception {
		mockMvc.perform(delete("/order/1"))
				.andExpect(status().isOk());
	}

	@Test
	void testGetOrderer() throws Exception {
		mockMvc.perform(get("/order/orderer"))
				.andExpect(status().isOk());
	}

	@Test
	void testGetExcel() throws Exception {
		when(orderService.getHistory()).thenReturn(new XSSFWorkbook());

		mockMvc.perform(get("/order/history"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "attachment; filename*=UTF-8''%EC%88%98%EC%A3%BC%20%EB%82%B4%EC%97%AD.xlsx"));
	}

}
