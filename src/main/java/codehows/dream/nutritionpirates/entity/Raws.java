package codehows.dream.nutritionpirates.entity;

import java.sql.Date;
import java.sql.Timestamp;

import codehows.dream.nutritionpirates.constants.RawProductName;
import codehows.dream.nutritionpirates.constants.RawsReason;
import codehows.dream.nutritionpirates.constants.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Raws {

	@Id
	@Column(name = "raws_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
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

	public void rawImport(Date importDate, Status status, Timestamp deadLine) {

		this.importDate = importDate;
		this.status = status;
		this.deadLine = deadLine;
	}

	public void rawExport(Date exportDate, Status status, RawsReason rawsReason) {
		/*this.exportDate = Date.valueOf(LocalDate.now());
		this.status = status.EXPORT;
		this.rawsReason = rawsReason.DISPOSE;*/
		this.exportDate = exportDate;
		this.status = status;
		this.rawsReason = rawsReason;
	}
}
