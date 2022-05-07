package bg.sofia.uni.fmi.mjt.food.analyzer.finder;

import bg.sofia.uni.fmi.mjt.food.analyzer.Food;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.FoodDataAPIException;
import bg.sofia.uni.fmi.mjt.food.analyzer.finder.response.ErrorResponse;
import bg.sofia.uni.fmi.mjt.food.analyzer.finder.response.SuccessResponse;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FoodDataCentralAPI implements FoodDataFinder {
    private static final String URI_SCHEME = "https";
    private static final String URI_AUTHORITY = "api.nal.usda.gov";
    private static final String URI_FOOD_SEARCH_PATH = "/fdc/v1/foods/search";
    private static final String URI_FOOD_DETAILS_PATH = "/fdc/v1/food/%s";

    private static final String NAME_SEARCH_KEY = "query";
    private static final String ALL_WORDS_KEY = "requireAllWords";
    private static final String PAGE_NUMBER_KEY = "pageNumber";
    private static final String PAGE_SIZE_KEY = "pageSize";
    private static final String API_KEY_KEY = "api_key";

    private static final String QUERY_PAIR_FORMAT = "%s=%s";
    private static final String QUERY_STRING_SEPARATOR = "&";

    private static final String UNABLE_TO_CONNECT = "Unable to connect to the Food Data Central API.";

    private static final int DEFAULT_FOOD_RESULTS = 50;
    private static final int MAX_FOOD_RESULTS = 10;

    private static final Gson GSON = new Gson();

    private final HttpClient foodAPIClient;
    private final String apiKey;

    public FoodDataCentralAPI(HttpClient foodAPIClient, String apiKey) {
        this.foodAPIClient = foodAPIClient;
        this.apiKey = apiKey;
    }

    @Override
    public Collection<Food> findByFoodName(String foodName) throws FoodDataAPIException {
        Collection<Food> result = new ArrayList<>();

        int pageNumber = 1;
        int totalHits;
        do {
            URI searchURI = buildSearchURI(foodName, pageNumber);
            HttpResponse<String> response = sendRequest(searchURI);

            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                ErrorResponse errorResponse = GSON.fromJson(response.body(), ErrorResponse.class);
                throw new FoodDataAPIException(errorResponse.getMessage());
            }

            SuccessResponse successResponse = GSON.fromJson(response.body(), SuccessResponse.class);
            result.addAll(successResponse.getFoods());

            totalHits = successResponse.getTotalHits();
            pageNumber++;
        } while (result.size() < totalHits && result.size() < MAX_FOOD_RESULTS);

        return List.copyOf(result);
    }

    @Override
    public Food findById(int id) throws FoodDataAPIException {
        URI detailsURI = buildDetailsURI(id);
        HttpResponse<String> response = sendRequest(detailsURI);

        if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            return null;
        }

        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            ErrorResponse errorResponse = GSON.fromJson(response.body(), ErrorResponse.class);
            throw new FoodDataAPIException(errorResponse.getMessage());
        }

        return GSON.fromJson(response.body(), Food.class);
    }

    private HttpResponse<String> sendRequest(URI uri) throws FoodDataAPIException {
        HttpRequest request = HttpRequest.newBuilder(uri).build();

        try {
            return foodAPIClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new FoodDataAPIException(UNABLE_TO_CONNECT, e);
        }
    }

    private URI buildSearchURI(String foodName, int pageNumber) {
        String foodNamePair = QUERY_PAIR_FORMAT.formatted(NAME_SEARCH_KEY, foodName);
        String allWordsPair = QUERY_PAIR_FORMAT.formatted(ALL_WORDS_KEY, "true");
        String pagePair = QUERY_PAIR_FORMAT.formatted(PAGE_NUMBER_KEY, pageNumber);
        String pageSizePair = QUERY_PAIR_FORMAT.formatted(PAGE_SIZE_KEY,
                Math.min(DEFAULT_FOOD_RESULTS, MAX_FOOD_RESULTS));
        String apiKeyPair = QUERY_PAIR_FORMAT.formatted(API_KEY_KEY, apiKey);

        String queryString = String.join(QUERY_STRING_SEPARATOR,
                List.of(foodNamePair, allWordsPair, pagePair, pageSizePair, apiKeyPair));

        try {
            return new URI(URI_SCHEME, URI_AUTHORITY, URI_FOOD_SEARCH_PATH, queryString, null);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Wrong URI!", e);
        }
    }

    private URI buildDetailsURI(int id) {
        String detailsPath = URI_FOOD_DETAILS_PATH.formatted(id);
        String apiKeyPair = QUERY_PAIR_FORMAT.formatted(API_KEY_KEY, apiKey);

        try {
            return new URI(URI_SCHEME, URI_AUTHORITY, detailsPath, apiKeyPair, null);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Wrong URI!", e);
        }
    }
}
