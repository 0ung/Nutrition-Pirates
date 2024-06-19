package codehows.dream.nutritionpirates.service;

import java.util.List;

import org.springframework.stereotype.Service;

import codehows.dream.nutritionpirates.entity.ProgramTime;
import codehows.dream.nutritionpirates.repository.ProgramTimeRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgramTimeService {
	private final ProgramTimeRepository programTimeRepository;

	public void registerTime() {
		ProgramTime programTime = new ProgramTime();
		try {
			programTimeRepository.save(programTime);
		} catch(RuntimeException e){
			throw new RuntimeException();
		}
	}

	public ProgramTime getProgramTime() {
		List<ProgramTime> list = programTimeRepository.findAll();
		if(list.isEmpty()){
			return null;
		}
		return list.get(0);
	}

	public String updateHourProgramTime(int hour) {
		List<ProgramTime> list = programTimeRepository.findAll();
		ProgramTime programTime = list.get(0);
		programTime.increaseHour(programTime.getCurrentProgramTime(), hour);

		return programTime.getFormattedCurrentProgramTime();
	}
}
