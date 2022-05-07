package bg.sofia.uni.fmi.mjt.food.analyzer.command;

import bg.sofia.uni.fmi.mjt.food.analyzer.cache.FoodCache;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.UnableToPerformOpException;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.InvalidRequestException;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.NoMatchesException;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public abstract class Command {
    private static final String REQUEST_NULL_OR_BLANK = "Request can't be null or blank.";
    private static final String COMMAND_SEPARATOR_NULL = "Command separator can't be null.";
    private static final String CACHE_NULL = "Food cache can't be null.";
    private static final String INVALID_COMMAND = "Invalid command.";

    protected static final Gson GSON = new Gson();

    protected List<String> args;

    protected Command(List<String> args) {
        this.args = args;
    }

    public abstract String execute() throws InvalidRequestException,
            NoMatchesException, UnableToPerformOpException;

    public static Command of(String request, String separator, FoodCache foodCache)
            throws InvalidRequestException {
        if (request == null || request.isBlank()) {
            throw new IllegalArgumentException(REQUEST_NULL_OR_BLANK);
        }

        if (separator == null) {
            throw new IllegalArgumentException(COMMAND_SEPARATOR_NULL);
        }

        if (foodCache == null) {
            throw new IllegalArgumentException(CACHE_NULL);
        }

        List<String> tokens = Arrays.stream(request.split(separator))
                .filter(t -> !t.isEmpty())
                .toList();

        return getCommand(tokens, foodCache);
    }

    private static Command getCommand(List<String> tokens, FoodCache foodCache)
            throws InvalidRequestException {
        FoodCommandType commandType = FoodCommandType.of(tokens.get(0));
        List<String> args = tokens.stream()
                .skip(1)
                .toList();

        return switch (commandType) {
            case GET_FOOD -> new GetFoodCommand(args, foodCache);
            case GET_FOOD_REPORT -> new FoodReportCommand(args, foodCache);
            case GET_FOOD_BY_GTIN_UPC -> new FoodByGtinUpcCommand(args, foodCache);
            case HELP -> new HelpCommand();
            case DISCONNECT -> new DisconnectCommand();
            case null -> throw new InvalidRequestException(INVALID_COMMAND);
        };
    }
}
