package bg.sofia.uni.fmi.mjt.food.analyzer.command;

import java.util.List;

public class DisconnectCommand extends Command {
    private static final String DISCONNECT_MESSAGE = "Disconnecting from the server. Goodbye!";

    public DisconnectCommand() {
        super(List.of());
    }

    @Override
    public String execute() {
        return DISCONNECT_MESSAGE;
    }
}
