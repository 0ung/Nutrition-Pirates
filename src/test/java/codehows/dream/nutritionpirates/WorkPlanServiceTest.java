package codehows.dream.nutritionpirates;

import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import codehows.dream.nutritionpirates.constants.FacilityStatus;
import codehows.dream.nutritionpirates.constants.Process;
import codehows.dream.nutritionpirates.entity.WorkPlan;
import codehows.dream.nutritionpirates.repository.WorkPlanRepository;
import codehows.dream.nutritionpirates.service.ProgramTimeService;
import codehows.dream.nutritionpirates.service.WorkPlanService;

@ExtendWith(MockitoExtension.class)
@TestPropertySource("classpath:application.yml")
public class WorkPlanServiceTest {

	@Mock
	private WorkPlanRepository workPlanRepository;

	@Mock
	private ProgramTimeService programTimeService;

	@InjectMocks
	private WorkPlanService workPlanService;

	@Test
	public void remainTime_ShouldActivateWorkPlan_WhenElapsedEnoughTime() {
		workPlanService.remainTime();
	}

	// Add more test cases to cover other scenarios, edge cases, and error conditions

}