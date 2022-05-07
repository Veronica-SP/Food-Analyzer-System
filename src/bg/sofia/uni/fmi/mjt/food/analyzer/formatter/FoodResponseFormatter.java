package bg.sofia.uni.fmi.mjt.food.analyzer.formatter;

import bg.sofia.uni.fmi.mjt.food.analyzer.Food;
import bg.sofia.uni.fmi.mjt.food.analyzer.command.FoodCommandType;
import bg.sofia.uni.fmi.mjt.food.analyzer.server.ServerResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class FoodResponseFormatter implements ServerResponseFormatter {
    private static final String COMMAND_SEPARATOR = " ";
    private static final String FOOD_COLLECTION_BANNER = "------------- FOOD %s -------------";
    private static final String FOOD_BANNER = "------------- FOOD -------------";

    private static final int OK_CODE = 200;

    private static final Gson GSON = new Gson();

    @Override
    public String format(String request, String response) {
        ServerResponse serverResponse = GSON.fromJson(response, ServerResponse.class);
        String responseBody = serverResponse.getBody();

        if (serverResponse.getCode() != OK_CODE) {
            return responseBody;
        }

        String command = request.split(COMMAND_SEPARATOR)[0];
        FoodCommandType commandType = FoodCommandType.of(command);

        return switch (commandType) {
            case GET_FOOD -> getFoodCollectionFormat(responseBody);
            case GET_FOOD_REPORT -> getFoodReportFormat(responseBody);
            case GET_FOOD_BY_GTIN_UPC -> getFoodFormat(responseBody);
            case HELP, DISCONNECT -> responseBody;
        };
    }

    private String getFoodCollectionFormat(String responseBody) {
        Type type = new TypeToken<List<Food>>() { }.getType();
        List<Food> result = GSON.fromJson(responseBody, type);

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < result.size(); i++) {
            stringBuilder.append(FOOD_COLLECTION_BANNER.formatted(i + 1))
                    .append(System.lineSeparator())
                    .append(result.get(i).byNameFormat());

            if (i < result.size() - 1) {
                stringBuilder.append(System.lineSeparator());
            }
        }

        return stringBuilder.toString();
    }

    private String getFoodReportFormat(String responseBody) {
        Food result = GSON.fromJson(responseBody, Food.class);
        return FOOD_BANNER + System.lineSeparator() + result.byIdFormat();
    }

    private String getFoodFormat(String responseBody) {
        Food result = GSON.fromJson(responseBody, Food.class);
        return FOOD_BANNER + System.lineSeparator() + result.toString();
    }
}
