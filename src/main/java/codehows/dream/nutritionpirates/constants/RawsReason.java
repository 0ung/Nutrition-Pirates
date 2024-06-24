package codehows.dream.nutritionpirates.constants;

public enum RawsReason {
	PROCESS_INPUT("공정투입"), DISPOSE("폐기");

	private final String value;

	RawsReason(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
}
