package codehows.dream.nutritionpirates.entity;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;

import codehows.dream.nutritionpirates.constants.RawProductName;
import codehows.dream.nutritionpirates.constants.RawsReason;
import codehows.dream.nutritionpirates.constants.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;

import static codehows.dream.nutritionpirates.constants.Status.WAITING;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Raws {

	@Id
	@Column(name = "raws_id")
	private String rawsCode;

	@Column(nullable = false)
	private String partner;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RawProductName product;

	@Column(nullable = false)
	private int quantity;


	//주문날짜
	@Column(nullable = false)
	//private Date orderDate;
	private Timestamp orderDate;

	//예상 입고일
	//private Date expectedImportDate;
	private Timestamp expectedImportDate;

	//입고
	private Date importDate;
	//private Timestamp importDate;

	//출고
	private Date exportDate;

	//사유
	@Enumerated(EnumType.STRING)
	private RawsReason rawsReason;

	//사용기한
	//private Date deadLine;
	private Timestamp deadLine;

	// 입고 상태 출고 상태 입고 대기 상태
	@Enumerated(EnumType.STRING)
	private Status status;


	public void rawImport() {
		//현재 날짜 Date 변환
		//this.importDate = Date.valueOf(LocalDate.now());

		Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
		this.importDate = new Date(currentTimestamp.getTime());
		this.status = status.IMPORT;

		// Date를 LocalDateTime으로 변환
		//LocalDateTime importDateTime = importDate.toLocalDate().atStartOfDay();
		//Calendar calendar = Calendar.getInstance();
		//calendar.setTime(importDate);
		//calendar.add(Calendar.DAY_OF_YEAR, 14);

		// 14일 더하기 (deadline 설정)
		LocalDateTime deadlineDateTime = currentTimestamp.toLocalDateTime().plusDays(14);

		// 다시 Timestamp 형태 받기
		this.deadLine = Timestamp.valueOf (deadlineDateTime);
		}

	public void rawExport() {
		this.exportDate = Date.valueOf(LocalDate.now());
		this.status = status.EXPORT;
		this.rawsReason = rawsReason.DISPOSE;
	}
}
