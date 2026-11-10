import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            Utils.logError("Usage: <strategy> [args]");
            System.exit(1);
        }

        int strategy = Parser.parseIntParameter(args[0], "strategy");
        if (strategy < 1 || strategy > 3) {
            Utils.logError("Invalid strategy.");
            return;
        }

        if (strategy == 1) {
            runStrategy1(args);
            return;
        } else if (strategy == 2) {
            runStrategy2(args);
            return;
        }
        runStrategy3(args);
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
        StrategySpecs specs = new StrategySpecs(args);
        List<LineData> lines = new FileReader(filename).readFile();

        Tree2D tree = new Tree2D(specs, lines);
        FileModule.saveToPNG(StrategySpecs.path(filename), tree.toImage(specs.lineWidth));
    }

    /**
     * Run our first own strategy.
     *
     * @param args The list of arguments to run the strategy.
     */
    private static void runStrategy2(String[] args) {
        if (args.length != 1 && args.length < 8) {
            Utils.logError("Strategy 2 requires either none or 8 arguments.");
            System.exit(1);
        }

        StrategySpecs specs = new StrategySpecs(args);
        int fileLength = specs.fileLength(args.length == 1 ? null : args[1]);
        List<LineData> lines = new FileGenerator(fileLength).generateFile(2);

        Tree2D tree = new Tree2D(specs, lines);
        FileModule.saveToPNG(specs.determineFromSpecs(2), tree.toImage(specs.lineWidth));
    }

    /**
     * Run our second own strategy.
     *
     * @param args The list of arguments to run the strategy.
     */
    private static void runStrategy3(String[] args) {
        if (args.length != 1 && args.length < 8) {
            Utils.logError("Strategy 3 requires either none or 8 arguments.");
            System.exit(1);
        }

        StrategySpecs specs = new StrategySpecs(args);
        int fileLength = specs.fileLength(args.length == 1 ? null : args[1]);
        List<LineData> lines = new FileGenerator(fileLength).generateFile(4);

        RQuadtree tree = new RQuadtree(specs, lines);
        FileModule.saveToPNG(specs.determineFromSpecs(2), tree.toImage(specs.lineWidth));
    }
}
