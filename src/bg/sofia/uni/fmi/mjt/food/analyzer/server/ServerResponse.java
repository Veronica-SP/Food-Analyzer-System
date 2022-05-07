package bg.sofia.uni.fmi.mjt.food.analyzer.server;

public class ServerResponse {
    private final int code;
    private final String body;

    public ServerResponse(int code, String body) {
        this.code = code;
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    public String getBody() {
        return body;
    }
}
