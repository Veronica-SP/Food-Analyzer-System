package bg.sofia.uni.fmi.mjt.food.analyzer.command;

import java.util.List;

public class HelpCommand extends Command {
    public HelpCommand() {
        super(List.of());
    }

    @Override
    public String execute() {
        return new StringBuilder()
                .append("Supported commands:")
                .append(System.lineSeparator())
                .append("get-food <food_name>: finds all foods that correspond to the specified food name")
                .append(System.lineSeparator())
                .append("get-food-report <food_fdcId>: finds a food corresponding to a valid FDC ID")
                .append(System.lineSeparator())
                .append("get-food-by-barcode --img=<barcode_image_file>|--code=<gtinUpc_code>: " +
                        "finds a food corresponding to the specified GTIN/UPC code or the provided " +
                        "barcode image file (1 parameter is required)")
                .append(System.lineSeparator())
                .append("disconnect: disconnects from the server and exits the program").toString();
    }
}
