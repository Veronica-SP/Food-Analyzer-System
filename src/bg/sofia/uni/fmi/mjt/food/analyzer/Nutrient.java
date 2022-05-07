package bg.sofia.uni.fmi.mjt.food.analyzer;

public class Nutrient {
    private final double value;

    public Nutrient(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
