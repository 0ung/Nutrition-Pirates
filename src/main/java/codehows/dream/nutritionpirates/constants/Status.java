package codehows.dream.nutritionpirates.constants;

public enum Status {
    WAITING("입고 대기"),
    IMPORT("입고"),
    EXPORT("출고");

    private final String value;

    Status(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
