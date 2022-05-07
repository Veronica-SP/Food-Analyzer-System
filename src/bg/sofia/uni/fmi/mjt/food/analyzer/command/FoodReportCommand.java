package bg.sofia.uni.fmi.mjt.food.analyzer.command;

import bg.sofia.uni.fmi.mjt.food.analyzer.Food;
import bg.sofia.uni.fmi.mjt.food.analyzer.cache.FoodCache;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.InvalidRequestException;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.NoMatchesException;

import java.util.List;

public class FoodReportCommand extends FoodCommand {
    private static final String INCORRECT_ARGS_COUNT = "Incorrect number of arguments for \"get-food-report\" command.";
    private static final String INVALID_ID_NUMBER = "FDC ID should be a valid number.";
    private static final String NO_MATCHES = "There isn't a food that matches the specified FDC ID.";

    public FoodReportCommand(List<String> args, FoodCache foodCache) {
        super(args, foodCache);
    }

    @Override
    public String execute() throws InvalidRequestException, NoMatchesException {
        if (args.size() != 1) {
            throw new InvalidRequestException(INCORRECT_ARGS_COUNT);
        }

        int id;
        try {
            id = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
            throw new InvalidRequestException(INVALID_ID_NUMBER, e);
        }

        Food food = foodCache.getById(id);
        if (food == null) {
            throw new NoMatchesException(NO_MATCHES);
        }

        return GSON.toJson(food);
    }
}
