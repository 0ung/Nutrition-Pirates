package codehows.dream.nutritionpirates.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Stock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "stock_id")
	private Long id;

	@OneToOne(fetch =  FetchType.LAZY)
	@JoinColumn(name = "work_plan_id")
	private WorkPlan workPlan;

	@Column(nullable = false)
	private int quantity;

	@Column(nullable = false)
	private Date createDate;

	private Date exportDate;

	private boolean isExport;

}
