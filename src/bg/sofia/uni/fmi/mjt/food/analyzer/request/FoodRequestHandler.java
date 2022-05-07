package bg.sofia.uni.fmi.mjt.food.analyzer.request;

import bg.sofia.uni.fmi.mjt.food.analyzer.cache.FoodCache;
import bg.sofia.uni.fmi.mjt.food.analyzer.command.Command;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.UnableToPerformOpException;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.InvalidRequestException;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.NoMatchesException;

public class FoodRequestHandler implements RequestHandler {
    private static final String FOOD_COMMAND_SEPARATOR = " ";
    private static final String REQUEST_NULL_OR_BLANK = "Request can't be null or blank.";

    private final FoodCache foodCache;

    public FoodRequestHandler(FoodCache foodCache) {
        this.foodCache = foodCache;
    }

    @Override
    public String execute(String request) throws InvalidRequestException,
            NoMatchesException, UnableToPerformOpException {
        if (request == null || request.isBlank()) {
            throw new IllegalArgumentException(REQUEST_NULL_OR_BLANK);
        }

        Command command = Command.of(request, FOOD_COMMAND_SEPARATOR, foodCache);
        return command.execute();
    }

    public void storeCache() {
        foodCache.store();
    }
}
