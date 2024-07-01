package codehows.dream.nutritionpirates;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import codehows.dream.nutritionpirates.controller.TimeController;
import codehows.dream.nutritionpirates.service.ProgramTimeService;

@WebMvcTest
@TestPropertySource(locations = "classpath:application_test.yml")
@ContextConfiguration(classes = TimeController.class)
public class ServerTimeTest {
	@Autowired
	private WebApplicationContext webApplicationContext;

	@MockBean
	private ProgramTimeService programTimeService;

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	}

	@DisplayName("시간 1시간 증가")
	@Test
	void updateTime1Hour() throws Exception {
		mockMvc.perform(patch("/time/1")
		).andExpect(status().isOk());
	}

	@DisplayName("시간 3시간 증가")
	void updateTime3Hour() throws Exception {
		mockMvc.perform(patch("/time/3")
		).andExpect(status().isOk());
	}
	@DisplayName("시간 6시간 증가")
	void updateTime6Hour() throws Exception {
		mockMvc.perform(patch("/time/6")
		).andExpect(status().isOk());
	}
}
