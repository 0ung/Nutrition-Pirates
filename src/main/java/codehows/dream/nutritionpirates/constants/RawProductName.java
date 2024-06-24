package codehows.dream.nutritionpirates.constants;



public enum RawProductName {
	CABBAGE("양배추"),
	BLACK_GARLIC("흑마늘"),
	POMEGRANATE("석류(농축액)"),
	PLUM("매실(농축액)"),
	HONEY("벌꿀"),
	COLLAGEN("콜라겐"),
	WRAPPING_PAPER("포장지"),
	BOX("박스");

	private final String value;

	RawProductName(String value) {
		this.value = value;
	}
	public String getValue(){
		return value;
	}
}
