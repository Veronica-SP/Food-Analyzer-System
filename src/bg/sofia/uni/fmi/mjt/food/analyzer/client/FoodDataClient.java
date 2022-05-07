package bg.sofia.uni.fmi.mjt.food.analyzer.client;

import bg.sofia.uni.fmi.mjt.food.analyzer.formatter.ServerResponseFormatter;

import java.net.InetSocketAddress;

public class FoodDataClient extends BaseClient {

    private static final String WELCOME_MESSAGE = "Welcome to the food analyzer system. " +
            "For more information on the supported commands use the command \"help\".";
    private static final String PROMPT = "> ";
    private static final String DISCONNECT_COMMAND = "disconnect";

    public FoodDataClient(InetSocketAddress socketAddress, ServerResponseFormatter formatter) {
        super(socketAddress, formatter, WELCOME_MESSAGE, PROMPT, DISCONNECT_COMMAND);
    }
}
