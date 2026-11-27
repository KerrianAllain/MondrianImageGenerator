
import java.util.List;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            Utils.logError("Usage: <strategy> [args]");
            System.exit(1);
            return;
        }

        final int strategy = Parser.parseIntParameter(args[0], "strategy");
        if (strategy < 0 || strategy > 3) {
            Utils.logError("Invalid strategy.");
            return;
        }

        if (strategy == 0) {
            runStrategy0(args);
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
     * Load a serialized tree and render it as an image.
     *
     * <p>
     * <b>Complexity: O(n + p)</b>, where {@code n} is the input size and
     * {@code p} is the number of pixels rendered.
     * </p>
     *
     * @param args Strategy number and text filename.
     */
    private static void runStrategy0(String[] args) {
        if (args.length != 2) {
            Utils.logError("Strategy 0 requires 1 argument.");
            System.exit(1);
        }

        String filename = args[1];
        Tree tree = Parser.parseTree(new FileReader(filename).readTreeText());
        FileModule.saveToPNG(StrategySpecs.path(filename), tree.toImage());
    }

    /**
     * Run the strategy given in the subject.
     *
     * <p>
     * <b>Complexity: O(n² + p)</b>, where {@code n} is the number of input
     * lines and {@code p} is the number of pixels rendered.
     * </p>
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
        saveTree(StrategySpecs.path(filename), "Tree2D", tree);
    }

    /**
     * Run the first custom strategy, using randomly generated binary-tree data.
     *
     * <p>
     * <b>Complexity: O(n² + p)</b>, where {@code n} is the generated data size
     * and {@code p} is the number of pixels rendered.
     * </p>
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
        saveTree(specs.determineFromSpecs(2), "Tree2D", tree);
    }

    /**
     * Run the second custom strategy, using a randomly generated RQuadtree.
     *
     * <p>
     * <b>Complexity: O(n² + p)</b>, where {@code n} is the generated data size
     * and {@code p} is the number of pixels rendered.
     * </p>
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
        saveTree(specs.determineFromSpecs(3), "RQuadtree", tree);
    }

    /**
     * Save the tree's text representation and rendered image.
     *
     * <p>
     * <b>Complexity: O(n + p)</b>, where {@code n} is the tree size and
     * {@code p} is the number of pixels rendered.
     * </p>
     *
     * @param filename The output name without an extension.
     * @param treeType The tree type used in the text filename.
     * @param tree The tree to save.
     */
    private static void saveTree(String filename, String treeType, Tree tree) {
        FileModule.saveToPNG(filename, tree.toImage());
        FileModule.saveToTXT(filename, treeType, tree);
    }
}
