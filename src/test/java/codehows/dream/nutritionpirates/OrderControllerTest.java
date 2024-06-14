package codehows.dream.nutritionpirates;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import codehows.dream.nutritionpirates.constants.ProductName;
import codehows.dream.nutritionpirates.controller.OrderController;
import codehows.dream.nutritionpirates.dto.MesOrderInsertDTO;

@SpringBootTest
// @TestPropertySource(locations = "classpath:application_test.yml")
@TestPropertySource(locations = "classpath:application.yml")
public class OrderControllerTest {

	@Autowired
	private OrderController orderController;



	@Test
	void testInsertOrder() {
		MesOrderInsertDTO mesOrderInsertDTO = new MesOrderInsertDTO();
		mesOrderInsertDTO.setOrderName("ABC상사");
		mesOrderInsertDTO.setOrderNumber("010-7574-3839");
		mesOrderInsertDTO.setProduct(ProductName.BLACK_GARLIC_JUICE);
		mesOrderInsertDTO.setQuantity(100);
		orderController.insertOrder(mesOrderInsertDTO);
	}

	@Test
	void testReadExcel(){

	}

}
