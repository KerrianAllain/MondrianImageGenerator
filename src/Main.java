import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            Utils.logError("Usage: <strategy> [args]");
            System.exit(1);
        }

        int strategy = Parser.parseIntParameter(args[0], "strategy");
        switch (strategy) {
            case 1:
                runStrategy1(args);
                break;

            case 2:
                runStrategy2(args);
                break;

            default:
                Utils.logError("Invalid strategy.");
                break;
        }
    }

    /**
     * Run the strategy given in the subject.
     *
     * @param args The list of arguments to run the strategy.
     */
    private static void runStrategy1(String[] args) {
        if (args.length < 8) {
            Utils.logError("Strategy 1 requires 8 arguments.");
            System.exit(1);
        }

        String filename = args[1];
        int width = Parser.parseIntParameter(args[2], "width");
        int height = Parser.parseIntParameter(args[3], "height");
        int nbLeavesToDivide = Parser.parseIntParameter(args[4], "nbLeafsToDivide");
        int minCutDimension = Parser.parseIntParameter(args[5], "minCutDimension");
        int lineWidth = Parser.parseIntParameter(args[6], "lineWidth");
        Variant variant = Parser.parseVariant(args[7]);

        List<LineData> lines = new FileReader(filename).readFile();
        Tree2D tree = new Tree2D(width, height, nbLeavesToDivide, minCutDimension, variant, lines);

        FileModule.saveToPNG(filename.replace(".txt", ""), tree.toImage(lineWidth));
    }

    /**
     * Run our own strategy.
     *
     * @param args The list of arguments to run the strategy.
     */
    private static void runStrategy2(String[] args) {
        if (args.length < 7) {
            Utils.logError("Strategy 2 requires 7 arguments.");
            System.exit(1);
        }

        int fileLength = Parser.parseIntParameter(args[1], "file length");
        int width = Parser.parseIntParameter(args[2], "width");
        int height = Parser.parseIntParameter(args[3], "height");
        int nbLeavesToDivide = Parser.parseIntParameter(args[4], "nbLeafsToDivide");
        int minCutDimension = Parser.parseIntParameter(args[5], "minCutDimension");
        int lineWidth = Parser.parseIntParameter(args[6], "lineWidth");
        Variant variant = Parser.parseVariant(args[7]);

        List<LineData> lines = new FileGenerator(fileLength).generateFile();
        String filename = String.join("-",
                "file",
                String.valueOf(args[0]),
                String.valueOf(width),
                String.valueOf(height),
                String.valueOf(nbLeavesToDivide),
                String.valueOf(minCutDimension),
                String.valueOf(lineWidth),
                String.valueOf(variant));
        Tree2D tree = new Tree2D(width, height, nbLeavesToDivide, minCutDimension, variant, lines);

        FileModule.saveToPNG(filename, tree.toImage(lineWidth));
    }
}
