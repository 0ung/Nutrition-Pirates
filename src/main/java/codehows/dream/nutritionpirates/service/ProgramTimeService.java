package codehows.dream.nutritionpirates.service;

import java.util.List;

import org.springframework.stereotype.Service;

import codehows.dream.nutritionpirates.entity.ProgramTime;
import codehows.dream.nutritionpirates.repository.ProgramTimeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgramTimeService {
	private final ProgramTimeRepository programTimeRepository;

	public ProgramTime getProgramTime() {
		List<ProgramTime> list = programTimeRepository.findAll();
		return list.get(0);
	}

	public void update1hourProgramTime(){
		List<ProgramTime> list = programTimeRepository.findAll();
		ProgramTime programTime = list.get(0);

		programTime.update

	}
}
