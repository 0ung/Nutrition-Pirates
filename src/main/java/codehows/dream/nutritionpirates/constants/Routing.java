package codehows.dream.nutritionpirates.constants;

import lombok.Getter;

@Getter
public class Routing {
	//세척
	public static final int WASHING_ROUTING_KG = 1333;
	public static final double WASHING_ROUTING_TIME = 2;
	public static final double WASHING_1KG_DURATION = 0.09;

	//inspection 검사
	public static final int INSPECTION_ROUTING = 5000;
	public static final double INSPECTION_ROUTING_TIME = 1;
	public static final double INSPECTION_1EA_DURATION = 0.012;
	public static final double INSPECTION_WAITING_TIME = 0.167;

	//BOX 포장
	public static final int BOX_PACKING_ROUTING = 160;
	public static final double BOX_PACKING_ROUTING_TIME = 1;
	public static final double BOX_PACKING_1EA_DURATION = 0.375;
	public static final double BOX_PACKING_WAITING_TIME = 0.5;

	//충진기(즙)
	public static final int JUICE_PACKING_ROUTING = 1250;
	public static final int JUICE_PACKING_ROUTING_TIME = 1;
	public static final double JUICE_1EA_DURATION = 0.048;
	public static final double JUICE_WAITING_TIME = 0.34;

	//충진기(스틱)
	public static final int STICK_PACKING_ROUTING = 2000;
	public static final int STICK_PACKING_ROUTING_TIME = 1;
	public static final double STICK_1EA_DURATION = 0.03;
	public static final double STICK_WAITING_TIME = 0.34;

	//추출 (cycle hour)
	public static final int EXTRACTION_ROUTING = 1000;
	public static final double EXTRACTION_ROUTING_TIME = 24;
	public static final double EXTRACTION_WAITING_TIME = 1;
	public static final double EXTRACTION_AFTER_TREATMENT = 24;

	//여과 (cycle hour)
	public static final int FILTER_ROUTING = 200;
	public static final double FILTER_ROUTING_TIME = 4;
	public static final double FILTER_WAITING_TIME = 1;

	//Sterilization 살균 (cycle hour)
	public static final int STERILIZATION_ROUTING = 100;
	public static final double STERILIZATION_ROUTING_TIME = 2;
	public static final double STERILIZATION_WAITING_TIME = 1;

	//혼합기 (cycle hour)
	public static final int MIX_ROUTING = 60;
	public static final double MIX_ROUTING_TIME = 1;
	public static final double MIX_WAITING_TIME = 1;

	public static final double FREEZE_TIME = 8;

}
