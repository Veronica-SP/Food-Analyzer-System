package bg.sofia.uni.fmi.mjt.food.analyzer.exceptions;

public class FoodDataAPIException extends Exception {
    public FoodDataAPIException(String message) {
        super(message);
    }

    public FoodDataAPIException(String message, Throwable cause) {
        super(message, cause);
    }
}
