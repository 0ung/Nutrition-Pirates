package codehows.dream.nutritionpirates.constants;

public enum Facility {
	juiceMachine1("J1"),
	juiceMachine2("J2"),
	StickMachine1("S1"),
	StickMachine2("S2"),
	extractor1("E1"),
	extractor2(
		"E2"),
	sterilizer1("S1"),
	sterilizer2("S2"),
	mixer("M"),
	filter("F"),
	boxMachine("B"),
	metalDetector("M"),
	washer("W"),
	weighing("W"),
	freeze("F");

	Facility(String value) {
		this.value = value;
	}

	private final String value;

	public static Facility[] getAllFacilities() {
		return Facility.values();
	}

	public String getValue() {
		return value;
	}
}
