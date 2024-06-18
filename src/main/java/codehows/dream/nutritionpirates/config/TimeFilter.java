package codehows.dream.nutritionpirates.config;

import java.io.IOException;

import org.springframework.stereotype.Component;

import codehows.dream.nutritionpirates.entity.ProgramTime;
import codehows.dream.nutritionpirates.service.ProgramTimeService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class TimeFilter implements Filter {

	private final ProgramTimeService programTimeService;

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
		IOException,
		ServletException {

		try {
			ProgramTime programTime = programTimeService.getProgramTime();
		} catch (IndexOutOfBoundsException e) {
			programTimeService.registerTime();
			log.info("시간 등록");
			filterChain.doFilter(servletRequest, servletResponse);
		}
		log.info("시간 존재");
		filterChain.doFilter(servletRequest, servletResponse);
	}
}
