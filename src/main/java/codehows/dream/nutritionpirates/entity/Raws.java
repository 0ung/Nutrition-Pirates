package codehows.dream.nutritionpirates.entity;

import java.sql.Date;
import java.time.LocalDate;
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
	private Date orderDate;

	//예상 입고일
	private Date expectedImportDate;

	//입고
	private Date importDate;

	//출고
	private Date exportDate;

	//사유
	@Enumerated(EnumType.STRING)
	private RawsReason rawsReason;

	//사용기한
	private Date deadLine;

	// 입고 상태 출고 상태 입고 대기 상태
	@Enumerated(EnumType.STRING)
	private Status status;

	public void rawImport() {
		this.importDate = Date.valueOf(LocalDate.now());
		this.status = status.IMPORT;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(importDate);
		calendar.add(Calendar.DAY_OF_YEAR, 14);

		this.deadLine = new Date (calendar.getTimeInMillis());
		}

	public void rawExport() {
		this.exportDate = Date.valueOf(LocalDate.now());
		this.status = status.EXPORT;
		this.rawsReason = rawsReason.DISPOSE;
	}
}
