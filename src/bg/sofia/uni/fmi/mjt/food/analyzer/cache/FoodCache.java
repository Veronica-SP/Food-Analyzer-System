package bg.sofia.uni.fmi.mjt.food.analyzer.cache;

import bg.sofia.uni.fmi.mjt.food.analyzer.Food;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.FoodDataAPIException;
import bg.sofia.uni.fmi.mjt.food.analyzer.finder.FoodDataFinder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FoodCache {
    private static final Gson GSON = new Gson();
    private static final String LOGS_FILE_NAME = "cache-logs.txt";

    private static final String LOGGER_FILE_HANDLER_ERROR =
            "Can't add file handler to logger.";
    private static final String CACHE_LOAD_ERROR_MESSAGE =
            "Unable to load cache from file.";
    private static final String CACHE_STORE_ERROR_MESSAGE =
            "Unable to store cache in file.";
    private static final String API_CONNECTION_ERROR_MESSAGE =
            "An error occurred while trying to fetch data from the Food Data Central API.";
    private static final String ERROR_MESSAGE_USER =
            "For more information please refer to the the logs in %s.";

    private final Path cacheFile;
    private final FoodDataFinder foodDataFinder;
    private final Logger logger;

    private Map<String, Collection<Food>> byFoodNameMap;
    private Map<Integer, Food> byIdMap;
    private Map<String, Food> byGtinUpcMap;

    public FoodCache(FoodDataFinder foodDataFinder, Path cacheFile) {
        this.foodDataFinder = foodDataFinder;
        this.cacheFile = cacheFile;
        this.logger = Logger.getLogger(FoodCache.class.getCanonicalName());

        try {
            this.logger.addHandler(new FileHandler(LOGS_FILE_NAME));
        } catch (IOException e) {
            throw new IllegalStateException(LOGGER_FILE_HANDLER_ERROR, e);
        }

        load();
    }

    public Collection<Food> getByFoodName(String foodName) {
        if (byFoodNameMap.containsKey(foodName)) {
            return byFoodNameMap.get(foodName);
        }

        Collection<Food> foods;
        try {
            foods = foodDataFinder.findByFoodName(foodName);
        } catch (FoodDataAPIException e) {
            logger.log(Level.WARNING, API_CONNECTION_ERROR_MESSAGE, e);
            System.out.println(API_CONNECTION_ERROR_MESSAGE +
                    ERROR_MESSAGE_USER.formatted(LOGS_FILE_NAME));

            return List.of();
        }

        byFoodNameMap.put(foodName, foods);
        foods.stream()
                .filter(f -> f.getGtinUpc() != null)
                .forEach(f -> byGtinUpcMap.putIfAbsent(f.getGtinUpc(), f));

        return foods;
    }

    public Food getById(int id) {
        if (byIdMap.containsKey(id)) {
            return byIdMap.get(id);
        }

        Food food;
        try {
            food = foodDataFinder.findById(id);
        } catch (FoodDataAPIException e) {
            logger.log(Level.WARNING, API_CONNECTION_ERROR_MESSAGE, e);
            System.out.println(API_CONNECTION_ERROR_MESSAGE +
                    ERROR_MESSAGE_USER.formatted(LOGS_FILE_NAME));

            return null;
        }

        byIdMap.put(id, food);
        if (food != null && food.getGtinUpc() != null) {
            byGtinUpcMap.put(food.getGtinUpc(), food);
        }

        return food;
    }

    public Food getByGtinUpc(String gtinUpc) {
        return byGtinUpcMap.get(gtinUpc);
    }

    public void store() {
        if (Files.notExists(cacheFile)) {
            try {
                Files.createFile(cacheFile);
            } catch (IOException e) {
                logger.log(Level.WARNING, CACHE_STORE_ERROR_MESSAGE, e);
                System.out.println(CACHE_STORE_ERROR_MESSAGE +
                        ERROR_MESSAGE_USER.formatted(LOGS_FILE_NAME));

                return;
            }
        }

        try (var foodCacheWriter = new PrintWriter(Files.newBufferedWriter(cacheFile), true)) {
            foodCacheWriter.println(GSON.toJson(byFoodNameMap));
            foodCacheWriter.println(GSON.toJson(byIdMap));
            foodCacheWriter.println(GSON.toJson(byGtinUpcMap));
        } catch (IOException e) {
            logger.log(Level.WARNING, CACHE_STORE_ERROR_MESSAGE, e);
            System.out.println(CACHE_STORE_ERROR_MESSAGE +
                    ERROR_MESSAGE_USER.formatted(LOGS_FILE_NAME));
        }
    }

    public Map<String, Collection<Food>> getByFoodNameMap() {
        return byFoodNameMap;
    }

    public Map<Integer, Food> getByIdMap() {
        return byIdMap;
    }

    public Map<String, Food> getByGtinUpcMap() {
        return byGtinUpcMap;
    }

    private void load() {
        if (Files.notExists(cacheFile)) {
            byFoodNameMap = new HashMap<>();
            byIdMap = new HashMap<>();
            byGtinUpcMap = new HashMap<>();
            return;
        }

        try (BufferedReader foodCacheReader = Files.newBufferedReader(cacheFile)) {
            loadByFoodName(foodCacheReader);
            loadById(foodCacheReader);
            loadByGtinUpc(foodCacheReader);
        } catch (IOException e) {
            logger.log(Level.WARNING, CACHE_LOAD_ERROR_MESSAGE, e);
            System.out.println(CACHE_LOAD_ERROR_MESSAGE +
                    ERROR_MESSAGE_USER.formatted(LOGS_FILE_NAME));

            byFoodNameMap = new HashMap<>();
            byIdMap = new HashMap<>();
            byGtinUpcMap = new HashMap<>();
        }
    }

    private void loadByFoodName(BufferedReader foodCacheReader) throws IOException {
        String byFoodNameJson = foodCacheReader.readLine();

        Type type = new TypeToken<Map<String, Collection<Food>>>() { }.getType();
        byFoodNameMap = GSON.fromJson(byFoodNameJson, type);
    }

    private void loadById(BufferedReader foodCacheReader) throws IOException {
        String byIdJson = foodCacheReader.readLine();

        Type type = new TypeToken<Map<Integer, Food>>() { }.getType();
        byIdMap = GSON.fromJson(byIdJson, type);
    }

    private void loadByGtinUpc(BufferedReader foodCacheReader) throws IOException {
        String byGtinUpcJson = foodCacheReader.readLine();

        Type type = new TypeToken<Map<String, Food>>() { }.getType();
        byGtinUpcMap = GSON.fromJson(byGtinUpcJson, type);
    }
}
