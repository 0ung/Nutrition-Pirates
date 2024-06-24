package codehows.dream.nutritionpirates.exception;

public class NotFoundOrderException extends RuntimeException {
	public NotFoundOrderException() {

	}

	public NotFoundOrderException(String message) {
		super(message);
	}
}
