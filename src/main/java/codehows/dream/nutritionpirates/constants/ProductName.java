package codehows.dream.nutritionpirates.constants;

public enum ProductName {
	CABBAGE_JUICE("양배추 즙"),
	BLACK_GARLIC_JUICE("흑마늘 즙"),
	POMEGRANATE_JELLY_STICK("석류 스틱"),
	PLUM_JELLY_STICK("매실 스틱");

	private final String value;

	ProductName(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
