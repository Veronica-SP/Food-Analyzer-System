package bg.sofia.uni.fmi.mjt.food.analyzer.command;

import bg.sofia.uni.fmi.mjt.food.analyzer.cache.FoodCache;

import java.util.List;

public abstract class FoodCommand extends Command {
    protected final FoodCache foodCache;

    protected FoodCommand(List<String> args, FoodCache foodCache) {
        super(args);
        this.foodCache = foodCache;
    }
}
