package bg.sofia.uni.fmi.mjt.food.analyzer;

public class LabelNutrients {
    private final Nutrient fat;
    private final Nutrient carbohydrates;
    private final Nutrient fiber;
    private final Nutrient protein;
    private final Nutrient calories;

    public LabelNutrients(Nutrient fat, Nutrient carbohydrates, Nutrient fiber, Nutrient protein, Nutrient calories) {
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.fiber = fiber;
        this.protein = protein;
        this.calories = calories;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("[fat: ").append(fat)
                .append(", carbohydrates: ").append(carbohydrates)
                .append(", fiber: ").append(fiber)
                .append(", protein: ").append(protein)
                .append(", calories: ").append(calories)
                .append("]").toString();
    }
}
