package codehows.dream.nutritionpirates.exception;

public class NoFoundProductName extends RuntimeException {
    public NoFoundProductName() {

    }

    public NoFoundProductName(String message) {
        super(message);
    }
}
