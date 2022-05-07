package bg.sofia.uni.fmi.mjt.food.analyzer.command;

import bg.sofia.uni.fmi.mjt.food.analyzer.Food;
import bg.sofia.uni.fmi.mjt.food.analyzer.cache.FoodCache;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.UnableToPerformOpException;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.InvalidRequestException;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.NoMatchesException;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FoodByGtinUpcCommand extends FoodCommand {
    private static final String NO_MATCHES =
            "There isn't a food that matches the specified GTIN/UPC code.";
    private static final String INCORRECT_ARGS_COUNT =
            "Incorrect number of arguments for \"get-food-by-barcode\" command.";
    private static final String INCORRECT_ARGS_FORMAT =
            "Incorrect argument format for \"get-food-by-barcode\" command.";
    private static final String IMAGE_DECODE_ERROR =
            "An error occurred while trying to decode the image file.";

    private static final String CODE_PREFIX = "--code=";
    private static final String IMG_PREFIX = "--img=";

    public FoodByGtinUpcCommand(List<String> args, FoodCache foodCache) {
        super(args, foodCache);
    }

    @Override
    public String execute() throws InvalidRequestException, UnableToPerformOpException, NoMatchesException {
        if (args.size() < 1 || args.size() > 2) {
            throw new InvalidRequestException(INCORRECT_ARGS_COUNT);
        }

        String joined = String.join(" ", args);
        int codeParamIndex = joined.indexOf(CODE_PREFIX);
        int imgParamIndex = joined.indexOf(IMG_PREFIX);

        String code;
        if (codeParamIndex != -1 && codeParamIndex + CODE_PREFIX.length() < joined.length()) {
            code = joined.substring(codeParamIndex + CODE_PREFIX.length());
        } else if (imgParamIndex != -1 && imgParamIndex + IMG_PREFIX.length() < joined.length()) {
            int lastIndex = joined.indexOf(' ') == -1 ? joined.length() : joined.indexOf(' ');
            Path barcodeFile = Path.of(joined.substring(imgParamIndex + IMG_PREFIX.length(), lastIndex));

            if (!Files.exists(barcodeFile)) {
                throw new InvalidRequestException(INCORRECT_ARGS_FORMAT);
            }

            try {
                code = decodeImage(barcodeFile);
            } catch (Exception e) {
                throw new UnableToPerformOpException(IMAGE_DECODE_ERROR, e);
            }
        } else {
            throw new InvalidRequestException(INCORRECT_ARGS_FORMAT);
        }

        Food food = foodCache.getByGtinUpc(code);
        if (food == null) {
            throw new NoMatchesException(NO_MATCHES);
        }

        return GSON.toJson(food);
    }

    String decodeImage(Path barcodeFile) throws IOException, ChecksumException, NotFoundException, FormatException {
        BufferedImage barcodeBufferedImage = ImageIO.read(barcodeFile.toFile());
        LuminanceSource luminanceSource = new BufferedImageLuminanceSource(barcodeBufferedImage);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));

        Reader reader = new MultiFormatReader();
        Result result = reader.decode(binaryBitmap);
        return result.getText();
    }
}
