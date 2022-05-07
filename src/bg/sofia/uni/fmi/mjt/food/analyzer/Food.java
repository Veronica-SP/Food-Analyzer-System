package bg.sofia.uni.fmi.mjt.food.analyzer;

import java.util.Objects;

public class Food {
    private final String description;
    private final int fdcId;
    private final String gtinUpc;
    private final String ingredients;
    private final LabelNutrients labelNutrients;

    public Food(String description, int fdcId, String gtinUpc, String ingredients, LabelNutrients labelNutrients) {
        this.description = description;
        this.fdcId = fdcId;
        this.gtinUpc = gtinUpc;
        this.ingredients = ingredients;
        this.labelNutrients = labelNutrients;
    }

    public String getGtinUpc() {
        return gtinUpc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Food food = (Food) o;
        return fdcId == food.fdcId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fdcId);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Description: ").append(description).append(System.lineSeparator())
                .append("FDC ID: ").append(fdcId).append(System.lineSeparator());

        if (gtinUpc != null) {
            result.append("GTIN/UPC: ").append(gtinUpc).append(System.lineSeparator());
        }

        if (ingredients != null) {
            result.append("Ingredients: ").append(ingredients).append(System.lineSeparator());
        }

        if (labelNutrients != null) {
            result.append("Label nutrients: ").append(labelNutrients).append(System.lineSeparator());
        }

        return result.toString();
    }

    public String byNameFormat() {
        StringBuilder result = new StringBuilder();
        result.append("Description: ").append(description).append(System.lineSeparator())
                .append("FDC ID: ").append(fdcId).append(System.lineSeparator());

        if (gtinUpc != null) {
            result.append("GTIN/UPC: ").append(gtinUpc).append(System.lineSeparator());
        }

        return result.toString();
    }

    public String byIdFormat() {
        StringBuilder result = new StringBuilder();
        result.append("Description: ").append(description).append(System.lineSeparator());

        if (ingredients != null) {
            result.append("Ingredients: ").append(ingredients).append(System.lineSeparator());
        }

        if (labelNutrients != null) {
            result.append("Label nutrients: ").append(labelNutrients).append(System.lineSeparator());
        }

        return result.toString();
    }
}
