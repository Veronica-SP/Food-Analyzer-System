package bg.sofia.uni.fmi.mjt.food.analyzer.command;

import bg.sofia.uni.fmi.mjt.food.analyzer.Food;
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

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetFoodCommandTest {
    private static final Gson GSON = new Gson();

    @Mock
    private FoodCache foodCacheMock;

    private Command invalidArgsCommand;
    private Command validCommand;

    @BeforeEach
    void setUp() throws InvalidRequestException {
        invalidArgsCommand = Command.of("get-food", " ", foodCacheMock);
        validCommand = Command.of("get-food beef jerky", " ", foodCacheMock);
    }

    @Test
    void testExecuteInvalidArgsCount() {
        assertThrows(InvalidRequestException.class,
                () -> invalidArgsCommand.execute(),
                "Command execution should throw InvalidRequestException " +
                        "when there are less than 1 arguments for \"get-food\" command!");
    }

    @Test
    void testExecuteNoMatches() {
        when(foodCacheMock.getByFoodName("beef jerky")).thenReturn(List.of());
        assertThrows(NoMatchesException.class,
                () ->validCommand.execute(),
                "Command execution should throw NoMatchesException when the food cache returns an empty collection!");
    }

    @Test
    void testExecuteExistingResults() throws NoMatchesException, InvalidRequestException, UnableToPerformOpException {
        Collection<Food> testFoods = getTestFoods();
        when(foodCacheMock.getByFoodName("beef jerky")).thenReturn(testFoods);

        assertEquals(GSON.toJson(testFoods), validCommand.execute(),
                "Wrong response for \"beef jerky\" food request!");
    }

    private Collection<Food> getTestFoods() {
        return List.of(new Food("beef jerky 1", 1, null, null, null),
                new Food("beef jerky 2", 2, null, null, null));
    }
}