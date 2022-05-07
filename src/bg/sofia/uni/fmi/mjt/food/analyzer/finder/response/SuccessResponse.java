package bg.sofia.uni.fmi.mjt.food.analyzer.finder.response;

import bg.sofia.uni.fmi.mjt.food.analyzer.Food;

import java.util.Collection;

public class SuccessResponse {
    private final int totalHits;
    private final Collection<Food> foods;

    public SuccessResponse(int totalHits, Collection<Food> foods) {
        this.totalHits = totalHits;
        this.foods = foods;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public Collection<Food> getFoods() {
        return foods;
    }
}
