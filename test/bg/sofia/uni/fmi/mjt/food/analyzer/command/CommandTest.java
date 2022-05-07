package bg.sofia.uni.fmi.mjt.food.analyzer.command;

import bg.sofia.uni.fmi.mjt.food.analyzer.cache.FoodCache;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommandTest {
    @Mock
    private FoodCache foodCacheMock;

    @Test
    void testFactoryRequestNullOrBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> Command.of(null, " ", foodCacheMock),
                "Command factory should throw IllegalArgumentException when invoked with null request!");
        assertThrows(IllegalArgumentException.class,
                () -> Command.of("  ", " ", foodCacheMock),
                "Command factory should throw IllegalArgumentException when invoked with blank request!");
    }

    @Test
    void testFactorySeparatorNull() {
        assertThrows(IllegalArgumentException.class,
                () -> Command.of("test-request", null, foodCacheMock),
                "Command factory should throw IllegalArgumentException when invoked with null command separator!");
    }

    @Test
    void testFactoryFoodCacheNull() {
        assertThrows(IllegalArgumentException.class,
                () -> Command.of("test-request", " ", null),
                "Command factory should throw IllegalArgumentException when invoked with null food cache!");
    }

    @Test
    void testFactoryGetFoodCommand() throws InvalidRequestException {
        Command result = Command.of("get-food", " ", foodCacheMock);
        assertTrue(result instanceof GetFoodCommand,
                "Command factory should return a GetFoodCommand when invoked with a \"get-food\" request!");

        Command resultCaseIns = Command.of("geT-fOoD", " ", foodCacheMock);
        assertTrue(resultCaseIns instanceof GetFoodCommand,
                "Commands should be case-insensitive!");
    }

    @Test
    void testFactoryGetFoodReportCommand() throws InvalidRequestException {
        Command result = Command.of("get-food-report", " ", foodCacheMock);
        assertTrue(result instanceof FoodReportCommand,
                "Command factory should return a FoodReportCommand when invoked with a \"get-food-report\" request!");

        Command resultCaseIns = Command.of("geT-fOoD-repORT", " ", foodCacheMock);
        assertTrue(resultCaseIns instanceof FoodReportCommand,
                "Commands should be case-insensitive!");
    }

    @Test
    void testFactoryGetByBarcodeCommand() throws InvalidRequestException {
        Command result = Command.of("get-food-by-barcode", " ", foodCacheMock);
        assertTrue(result instanceof FoodByGtinUpcCommand,
                "Command factory should return a FoodByGtinUpcCommand when invoked with a \"get-food-by-barcode\" request!");

        Command resultCaseIns = Command.of("GET-food-BY-barcode", " ", foodCacheMock);
        assertTrue(resultCaseIns instanceof FoodByGtinUpcCommand,
                "Commands should be case-insensitive!");
    }

    @Test
    void testFactoryHelpCommand() throws InvalidRequestException {
        Command result = Command.of("help", " ", foodCacheMock);
        assertTrue(result instanceof HelpCommand,
                "Command factory should return a HelpCommand when invoked with a \"help\" request!");

        Command resultCaseIns = Command.of("hELP", " ", foodCacheMock);
        assertTrue(resultCaseIns instanceof HelpCommand,
                "Commands should be case-insensitive!");
    }

    @Test
    void testFactoryDisconnectCommand() throws InvalidRequestException {
        Command result = Command.of("disconnect", " ", foodCacheMock);
        assertTrue(result instanceof DisconnectCommand,
                "Command factory should return a DisconnectCommand when invoked with a \"disconnect\" request!");

        Command resultCaseIns = Command.of("DISCONNECt", " ", foodCacheMock);
        assertTrue(resultCaseIns instanceof DisconnectCommand,
                "Commands should be case-insensitive!");
    }

    @Test
    void testFactoryInvalidRequest() {
        assertThrows(InvalidRequestException.class,
                () -> Command.of("some-request", " ", foodCacheMock),
                "Command factory should throw InvalidRequestException when invoked with invalid request");
    }
}