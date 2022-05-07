package bg.sofia.uni.fmi.mjt.food.analyzer.finder;

import bg.sofia.uni.fmi.mjt.food.analyzer.Food;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.FoodDataAPIException;

import java.util.Collection;

public interface FoodDataFinder {

    /**
     * Finds all the foods that correspond to the specified food name.
     *
     * @param foodName - the food name by which the search is performed.
     *
     * @throws  FoodDataAPIException if an error occurs while trying to connect to the API
     *
     * @return an unmodifiable collection with the found foods.
     *         In case there are none - returns an empty collection.
     */
    Collection<Food> findByFoodName(String foodName) throws FoodDataAPIException;

    /**
     * Finds the food that corresponds to the specified id
     *
     * @param id - the id by which the search is performed.
     *
     * @throws  FoodDataAPIException if an error occurs while trying to connect to the API
     *
     * @return the food corresponding to the id.
     *         In case there is no such food - returns null.
     */
    Food findById(int id) throws FoodDataAPIException;
}
