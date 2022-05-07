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

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodByGtinUpcCommandTest {
    private static final Gson GSON = new Gson();
    private static final Path validImg = Path.of("barcode.gif");
    private static final Food testFood = new Food("food1", 1, "1234", null, null);

    @Mock
    private FoodCache foodCacheMock;

    private Command noArgsCommand;
    private Command moreThanTwoArgsCommand;

    private Command noCodeOrImageCommand;

    private Command onlyCodeInvalidCommand;
    private Command onlyCodeValidCommand;

    private Command onlyImageInvalidCommand;
    private Command onlyImageCantDecodeCommand;
    private Command onlyImageValidCommand;

    private Command bothArgsInvalidCommand;
    private Command bothArgsCodeInvalidCommand;
    private Command bothArgsImgInvalidCommand;
    private Command bothArgsBothValidCommand;

    @BeforeEach
    void setUp() throws InvalidRequestException {
        noArgsCommand = Command.of("get-food-by-barcode", " ", foodCacheMock);
        moreThanTwoArgsCommand = Command.of("get-food-by-barcode a1 a2 a3", " ", foodCacheMock);

        noCodeOrImageCommand = Command.of("get-food-by-barcode a1 a2", " ", foodCacheMock);

        onlyCodeInvalidCommand = Command.of("get-food-by-barcode --code=", " ", foodCacheMock);
        onlyCodeValidCommand = Command.of("get-food-by-barcode --code=1234", " ", foodCacheMock);

        onlyImageInvalidCommand = Command.of("get-food-by-barcode --img=1234", " ", foodCacheMock);
        onlyImageCantDecodeCommand = Command.of("get-food-by-barcode --img=not_an_image.txt", " ", foodCacheMock);
        onlyImageValidCommand = Command.of("get-food-by-barcode --img=barcode.gif", " ", foodCacheMock);

        bothArgsInvalidCommand = Command.of("get-food-by-barcode --img=12234 --code=", " ", foodCacheMock);
        bothArgsCodeInvalidCommand = Command.of("get-food-by-barcode --img=barcode.gif --code=", " ", foodCacheMock);
        bothArgsImgInvalidCommand = Command.of("get-food-by-barcode --img=1234 --code=1234", " ", foodCacheMock);
        bothArgsBothValidCommand = Command.of("get-food-by-barcode --img=barcode.gif --code=1234", " ", foodCacheMock);
    }

    @Test
    void testExecuteNoArgs() {
        assertThrows(InvalidRequestException.class,
                () -> noArgsCommand.execute(),
                "Command execution should throw InvalidRequestException " +
                        "when there are less than 1 arguments for \"get-food-by-barcode\" command!");
    }

    @Test
    void testExecuteMoreThanTwoArgs() {
        assertThrows(InvalidRequestException.class,
                () -> moreThanTwoArgsCommand.execute(),
                "Command execution should throw InvalidRequestException " +
                        "when there is more than 2 arguments for \"get-food-by-barcode\" command!");
    }

    @Test
    void testExecuteNoCodeOrImageArg() {
        assertThrows(InvalidRequestException.class,
                () -> noCodeOrImageCommand.execute(),
                "Command execution should throw InvalidRequestException " +
                        "when there is no code or img argument specified for \"get-food-by-barcode\" command!");
    }

    @Test
    void testExecuteWhenOnlyCodeArgInvalid() {
        assertThrows(InvalidRequestException.class,
                () -> onlyCodeInvalidCommand.execute(),
                "Command execution should throw InvalidRequestException " +
                        "when there is only an invalid code argument for \"get-food-by-barcode\" command!");
    }

    @Test
    void testExecuteWhenOnlyImgArgInvalid() {
        assertThrows(InvalidRequestException.class,
                () -> onlyImageInvalidCommand.execute(),
                "Command execution should throw InvalidRequestException " +
                        "when the image path is invalid \"get-food-by-barcode\" command!");
    }

    @Test
    void testExecuteWhenOnlyImgArgNotAnImage() {
        assertThrows(UnableToPerformOpException.class,
                () -> onlyImageCantDecodeCommand.execute(),
                "Command execution should throw UnableToPerformOpException " +
                        "when the file can't be decoded \"get-food-by-barcode\" command!");
    }

    @Test
    void testExecuteWhenBothArgsInvalid() {
        assertThrows(InvalidRequestException.class,
                () -> bothArgsInvalidCommand.execute(),
                "Command execution should throw InvalidRequestException " +
                        "when both the code and the image path are invalid for \"get-food-by-barcode\" command!");
    }

    @Test
    void testExecuteOnlyCodeValid() throws NoMatchesException, InvalidRequestException, UnableToPerformOpException {
        when(foodCacheMock.getByGtinUpc("1234")).thenReturn(testFood);
        assertEquals(GSON.toJson(testFood), onlyCodeValidCommand.execute(),
                "Wrong response for valid code argument!");
    }

    @Test
    void testExecuteOnlyImageValid() throws NoMatchesException, InvalidRequestException, UnableToPerformOpException {
        when(foodCacheMock.getByGtinUpc("061500003632")).thenReturn(testFood);
        assertEquals(GSON.toJson(testFood), onlyImageValidCommand.execute(),
                "Wrong response for valid img argument!");
    }

    @Test
    void testExecuteBothArgsCodeInvalid() throws NoMatchesException, InvalidRequestException, UnableToPerformOpException {
        when(foodCacheMock.getByGtinUpc("061500003632")).thenReturn(testFood);
        assertEquals(GSON.toJson(testFood), bothArgsCodeInvalidCommand.execute(),
                "When both of the arguments are specified and the code is invalid the image parameter should be taken!");
    }

    @Test
    void testExecuteBothArgsImageInvalid() throws NoMatchesException, InvalidRequestException, UnableToPerformOpException {
        when(foodCacheMock.getByGtinUpc("1234")).thenReturn(testFood);
        assertEquals(GSON.toJson(testFood), bothArgsImgInvalidCommand.execute(),
                "When both of the arguments are specified and the code is valid the code parameter should be taken regardless of the image!");
    }

    @Test
    void testExecuteBothArgsBothValid() throws NoMatchesException, InvalidRequestException, UnableToPerformOpException {
        when(foodCacheMock.getByGtinUpc("1234")).thenReturn(testFood);
        assertEquals(GSON.toJson(testFood), bothArgsBothValidCommand.execute(),
                "When both of the arguments are specified and valid the code parameter should be taken!");
    }

    @Test
    void testExecuteValidArgsNoMatch() {
        when(foodCacheMock.getByGtinUpc("1234")).thenReturn(null);
        assertThrows(NoMatchesException.class,
                () -> bothArgsBothValidCommand.execute(),
                "Command execution should throw NoMatchesException if the food cache returns null!");
    }
}