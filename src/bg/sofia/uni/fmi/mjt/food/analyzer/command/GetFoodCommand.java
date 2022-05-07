package bg.sofia.uni.fmi.mjt.food.analyzer.command;

import bg.sofia.uni.fmi.mjt.food.analyzer.Food;
import bg.sofia.uni.fmi.mjt.food.analyzer.cache.FoodCache;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.InvalidRequestException;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.NoMatchesException;

import java.util.Collection;
import java.util.List;

public class GetFoodCommand extends FoodCommand {
    private static final String NAME_WORDS_SEPARATOR = " ";
    private static final String NO_MATCHES = "There are no foods that match the specified food name.";
    private static final String INCORRECT_ARGS_COUNT = "Incorrect number of arguments for \"get-food\" command.";

    public GetFoodCommand(List<String> args, FoodCache foodCache) {
        super(args, foodCache);
    }

    @Override
    public String execute() throws InvalidRequestException, NoMatchesException {
        if (args.size() < 1) {
            throw new InvalidRequestException(INCORRECT_ARGS_COUNT);
        }

        Collection<Food> foods = foodCache.getByFoodName(String.join(NAME_WORDS_SEPARATOR, args));
        if (foods.isEmpty()) {
            throw new NoMatchesException(NO_MATCHES);
        }

        return GSON.toJson(foods);
    }
}
