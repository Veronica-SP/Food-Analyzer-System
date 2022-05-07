package bg.sofia.uni.fmi.mjt.food.analyzer.command;

import bg.sofia.uni.fmi.mjt.food.analyzer.Food;
import bg.sofia.uni.fmi.mjt.food.analyzer.LabelNutrients;
import bg.sofia.uni.fmi.mjt.food.analyzer.Nutrient;
import bg.sofia.uni.fmi.mjt.food.analyzer.cache.FoodCache;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.InvalidRequestException;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.NoMatchesException;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.UnableToPerformOpException;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodReportCommandTest {
    private static final Gson GSON = new Gson();

    @Mock
    private FoodCache foodCacheMock;

    private Command noArgsCommand;
    private Command moreThanOneArgCommand;
    private Command invalidIdNumberCommand;
    private Command validCommand;

    @BeforeEach
    void setUp() throws InvalidRequestException {
        noArgsCommand = Command.of("get-food-report", " ", foodCacheMock);
        moreThanOneArgCommand = Command.of("get-food-report 1234 test", " ", foodCacheMock);
        invalidIdNumberCommand = Command.of("get-food-report abc123", " ", foodCacheMock);
        validCommand = Command.of("get-food-report 1234", " ", foodCacheMock);
    }

    @Test
    void testExecuteNoArgs() {
        assertThrows(InvalidRequestException.class,
                () -> noArgsCommand.execute(),
                "Command execution should throw InvalidRequestException " +
                        "when there are less than 1 arguments for \"get-food-report\" command!");
    }

    @Test
    void testExecuteMoreThanOneArg() {
        assertThrows(InvalidRequestException.class,
                () -> moreThanOneArgCommand.execute(),
                "Command execution should throw InvalidRequestException " +
                        "when there is more than 1 argument for \"get-food-report\" command!");
    }

    @Test
    void testExecuteInvalidIdNumber() {
        assertThrows(InvalidRequestException.class,
                () -> invalidIdNumberCommand.execute(),
                "Command execution should throw InvalidRequestException " +
                        "when the fdcId can't be parsed to a valid number!");
    }

    @Test
    void testExecuteNoMatches() {
        when(foodCacheMock.getById(1234)).thenReturn(null);
        assertThrows(NoMatchesException.class,
                () ->validCommand.execute(),
                "Command execution should throw NoMatchesException when the food cache returns null!");
    }

    @Test
    void testExecuteExistingResults() throws NoMatchesException, InvalidRequestException, UnableToPerformOpException {
        Food testFood = new Food("food1", 1234, "123456", "",
                new LabelNutrients(new Nutrient(1.0), new Nutrient(1.0), new Nutrient(1.0), new Nutrient(1.0), new Nutrient(1.0)));
        when(foodCacheMock.getById(1234)).thenReturn(testFood);

        assertEquals(GSON.toJson(testFood), validCommand.execute(),
                "Wrong response for \"1234\" FDC ID!");
    }
}