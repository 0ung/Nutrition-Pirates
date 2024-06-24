package codehows.dream.nutritionpirates.exception;

public class NoFoundProductNameException extends RuntimeException {
    public NoFoundProductNameException() {

    }

    public NoFoundProductNameException(String message) {
        super(message);
    }
}
