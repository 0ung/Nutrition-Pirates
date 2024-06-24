package codehows.dream.nutritionpirates.entity;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "program_time")
public class ProgramTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Timestamp currentProgramTime;

	public void increaseHour(int hour) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.currentProgramTime);
		calendar.add(Calendar.HOUR, hour);

		this.currentProgramTime = new Timestamp(calendar.getTime().getTime());
	}

	public void setCurrentProgramTime(Date date) {
		this.currentProgramTime = new Timestamp(date.getTime());
	}

	public String getFormattedCurrentProgramTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(currentProgramTime);
	}

	public ProgramTime() {
		this.currentProgramTime = Timestamp.valueOf(LocalDateTime.now());
	}

}
