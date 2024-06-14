package codehows.dream.nutritionpirates.entity;

import java.sql.Date;

import codehows.dream.nutritionpirates.constants.RawProductName;
import codehows.dream.nutritionpirates.constants.RawsReason;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

	@Builder
	public Raws(String rawsCode,String partner, RawProductName product, int quantity, Date orderDate) {
		this.rawsCode = rawsCode;
		this.partner = partner;
		this.product = product;
		this.quantity = quantity;
		this.orderDate = orderDate;
	}
}
