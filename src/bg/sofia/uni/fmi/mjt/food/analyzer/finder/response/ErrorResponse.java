package bg.sofia.uni.fmi.mjt.food.analyzer.finder.response;

public class ErrorResponse {
    private final String code;
    private final String message;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
