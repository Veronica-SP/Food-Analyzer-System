package bg.sofia.uni.fmi.mjt.food.analyzer.exceptions;

public class UnableToPerformOpException extends Exception {
    public UnableToPerformOpException(String message) {
        super(message);
    }

    public UnableToPerformOpException(String message, Throwable cause) {
        super(message, cause);
    }
}
