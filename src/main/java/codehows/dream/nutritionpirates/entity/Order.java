package codehows.dream.nutritionpirates.entity;

import codehows.dream.dummy.constants.ProductName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "mes_order")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long id;

	//제품명
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ProductName product;

	@Column(nullable = false)
	private int quantity;

	private boolean urgency;

	private boolean individual;

	@Column(nullable = false)
	private String expectedDeliveryDate;

	@JoinColumn(name = "orderer_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Orderer orderer;

	private boolean isShipping;

}
