package bg.sofia.uni.fmi.mjt.food.analyzer.exceptions;

public class NoMatchesException extends Exception {
    public NoMatchesException(String message) {
        super(message);
    }

    public NoMatchesException(String message, Throwable cause) {
        super(message, cause);
    }
}
