package bg.sofia.uni.fmi.mjt.food.analyzer.command;

public enum FoodCommandType {
    GET_FOOD,
    GET_FOOD_REPORT,
    GET_FOOD_BY_GTIN_UPC,
    HELP,
    DISCONNECT;

    public static FoodCommandType of(String text) {
        return switch (text.toLowerCase()) {
            case "get-food" -> GET_FOOD;
            case "get-food-report" -> GET_FOOD_REPORT;
            case "get-food-by-barcode" -> GET_FOOD_BY_GTIN_UPC;
            case "help" -> HELP;
            case "disconnect" -> DISCONNECT;
            default -> null;
        };
    }
}
