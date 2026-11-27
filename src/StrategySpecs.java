import java.nio.file.Paths;
import java.util.Objects;
import java.util.Random;

public class StrategySpecs {
    private static final String DEFAULT_FILENAME = "file";
    private static final Random RANDOM = new Random();

    public int nbLeavesToDivide;
    public int minCutDimension;
    public Variant variant;
    public int lineWidth;
    public int height;
    public int width;

    public StrategySpecs(String[] args) {
        if (args.length == 8) {
            this.parseFromArguments(args);
            return;
        }
        generateArguments();
    }

    private void parseFromArguments(String[] args) {
        this.width = Parser.parseIntParameter(args[2], "width");
        this.height = Parser.parseIntParameter(args[3], "height");
        this.nbLeavesToDivide = Parser.parseIntParameter(args[4], "nbLeavesToDivide");
        this.minCutDimension = Parser.parseIntParameter(args[5], "minCutDimension");
        this.lineWidth = Parser.parseIntParameter(args[6], "lineWidth");
        this.variant = Parser.parseVariant(args[7]);
    }

    private void generateArguments() {
        this.width = Utils.roundToNearestTen(Utils.generateInt(RANDOM, 50, 200));
        this.height = Utils.roundToNearestTen(Utils.generateInt(RANDOM, 50, 200));
        this.nbLeavesToDivide = Utils.generateInt(RANDOM, 12, 60);
        this.minCutDimension = Utils.generateInt(RANDOM, 2, 10);
        this.lineWidth = Utils.generateInt(RANDOM, 1, minCutDimension / 2);
        this.variant = (Utils.generateInt(RANDOM, 0, 1) == 0) ? Variant.A : Variant.B;
    }

    /**
     * Determine the filename from the given strategy and current specifications.
     *
     * @param strategy The strategy number used.
     * @return The formatted filename.
     */
    public String determineFromSpecs(int strategy) {
        return String.join("-",
                DEFAULT_FILENAME,
                String.valueOf(strategy),
                String.valueOf(width),
                String.valueOf(height),
                String.valueOf(nbLeavesToDivide),
                String.valueOf(minCutDimension),
                String.valueOf(lineWidth),
                String.valueOf(variant));
    }

    /**
     * Determine the length of the file to generate.
     *
     * @param value The value to parse.
     * @return The parsed length.
     */
    public int fileLength(String value) {
        return Objects.isNull(value) ? this.nbLeavesToDivide : Parser.parseIntParameter(value, "file length");
    }

    /**
     * Determine the filename from the given path.
     * Uses Regex expression to delete the first .txt extension found.
     *
     * @param filename The path to the file content.
     * @return The cleaned filename.
     */
    public static String path(String filename) {
        return Paths.get(filename).getFileName().toString().replaceFirst("\\.txt$", "");
    }
}
