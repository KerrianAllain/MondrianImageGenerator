import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            Utils.logError("Usage: <strategy> [args]");
            System.exit(1);
            return;
        }

        try {
            switch (parseStrategy(args[0])) {
                case 0:
                    runStrategy0(args);
                    break;
                case 1:
                    runStrategy1(args);
                    break;
                case 2:
                    runStrategy2(args);
                    break;
                case 3:
                    runStrategy3(args);
                    break;
                default:
                    throw new IllegalArgumentException("Strategy must be between 0 and 3.");
            }
        } catch (IllegalArgumentException exception) {
            Utils.logError(exception.getMessage());
            System.exit(1);
        }
    }

    /**
     * Parse the strategy number.
     *
     * <p><b>Complexity: O(n)</b>, where {@code n} is the argument length.</p>
     *
     * @param value The command-line strategy argument.
     * @return The strategy number, from 0 to 3.
     */
    private static int parseStrategy(String value) {
        final int strategy;
        try {
            strategy = Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Strategy must be an integer between 0 and 3.", exception);
        }
        if (strategy < 0 || strategy > 3)
            throw new IllegalArgumentException("Strategy must be between 0 and 3.");
        return strategy;
    }

    /**
     * Load a serialized tree and render it as an image.
     *
     * <p><b>Complexity: O(n + p)</b>, where {@code n} is the input size and
     * {@code p} is the number of pixels rendered.</p>
     *
     * @param args Strategy number, text filename, and optional line width.
     */
    private static void runStrategy0(String[] args) {
        if (args.length < 2 || args.length > 3)
            throw new IllegalArgumentException("Strategy 0 usage: 0 <tree.txt> [lineWidth].");

        String filename = args[1];
        int lineWidth = args.length == 3 ? parseLineWidth(args[2]) : 0;
        Tree tree = parseTree(readTreeText(filename));
        FileModule.saveToPNG(StrategySpecs.path(filename), tree.toImage(lineWidth));
    }

    /**
     * Parse the optional line width for strategy 0.
     *
     * <p><b>Complexity: O(n)</b>, where {@code n} is the argument length.</p>
     *
     * @param value The line width argument.
     * @return A non-negative line width.
     */
    private static int parseLineWidth(String value) {
        final int lineWidth;
        try {
            lineWidth = Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Line width must be a non-negative integer.", exception);
        }
        if (lineWidth < 0)
            throw new IllegalArgumentException("Line width must be a non-negative integer.");
        return lineWidth;
    }

    /**
     * Read a serialized tree from a UTF-8 text file.
     *
     * <p><b>Complexity: O(n)</b>, where {@code n} is the file size.</p>
     *
     * @param filename The path to the text file.
     * @return The trimmed tree representation.
     */
    private static String readTreeText(String filename) {
        try {
            return new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8).trim();
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to read tree file '" + filename + "'.", exception);
        }
    }

    /**
     * Reconstruct a tree according to the type marker in its text format.
     *
     * <p><b>Complexity: O(n)</b>, where {@code n} is the representation length.</p>
     *
     * @param representation The serialized tree.
     * @return The reconstructed binary tree or RQuadtree.
     */
    private static Tree parseTree(String representation) {
        if (representation.startsWith("T2D("))
            return new Tree2D(representation);
        if (representation.startsWith("RQT("))
            return new RQuadtree(representation);
        throw new IllegalArgumentException("The file does not contain a supported tree representation.");
    }

    /**
     * Run the strategy given in the subject.
     *
     * <p><b>Complexity: O(n² + p)</b>, where {@code n} is the number of input
     * lines and {@code p} is the number of pixels rendered.</p>
     *
     * @param args The list of arguments to run the strategy.
     */
    private static void runStrategy1(String[] args) {
        if (args.length != 8)
            throw new IllegalArgumentException("Strategy 1 requires 8 arguments.");

        String filename = args[1];
        StrategySpecs specs = new StrategySpecs(args);
        List<LineData> lines = parseLines(filename);

        Tree2D tree = new Tree2D(specs, lines);
        saveTree(StrategySpecs.path(filename), "Tree2D", tree, specs.lineWidth);
    }

    /**
     * Run the first custom strategy, using randomly generated binary-tree data.
     *
     * <p><b>Complexity: O(n² + p)</b>, where {@code n} is the generated data
     * size and {@code p} is the number of pixels rendered.</p>
     *
     * @param args The list of arguments to run the strategy.
     */
    private static void runStrategy2(String[] args) {
        if (args.length != 1 && args.length != 8)
            throw new IllegalArgumentException("Strategy 2 requires either 1 or 8 arguments.");

        StrategySpecs specs = new StrategySpecs(args);
        int fileLength = specs.fileLength(args.length == 1 ? null : args[1]);
        List<LineData> lines = new FileGenerator(fileLength).generateFile(2);

        Tree2D tree = new Tree2D(specs, lines);
        saveTree(specs.determineFromSpecs(2), "Tree2D", tree, specs.lineWidth);
    }

    /**
     * Run the second custom strategy, using a randomly generated RQuadtree.
     *
     * <p><b>Complexity: O(n² + p)</b>, where {@code n} is the generated data
     * size and {@code p} is the number of pixels rendered.</p>
     *
     * @param args The list of arguments to run the strategy.
     */
    private static void runStrategy3(String[] args) {
        if (args.length != 1 && args.length != 8)
            throw new IllegalArgumentException("Strategy 3 requires either 1 or 8 arguments.");

        StrategySpecs specs = new StrategySpecs(args);
        int fileLength = specs.fileLength(args.length == 1 ? null : args[1]);
        List<LineData> lines = new FileGenerator(fileLength).generateFile(4);

        RQuadtree tree = new RQuadtree(specs, lines);
        saveTree(specs.determineFromSpecs(3), "RQuadtree", tree, specs.lineWidth);
    }

    /**
     * Read line data from an input file.
     *
     * <p><b>Complexity: O(n)</b>, where {@code n} is the input file size.</p>
     *
     * @param filename The path to the input file.
     * @return The parsed input lines.
     */
    private static List<LineData> parseLines(String filename) {
        return new FileReader(filename).readFile();
    }

    /**
     * Save the tree's text representation and rendered image.
     *
     * <p><b>Complexity: O(n + p)</b>, where {@code n} is the tree size and
     * {@code p} is the number of pixels rendered.</p>
     *
     * @param filename The output name without an extension.
     * @param treeType The tree type used in the text filename.
     * @param tree The tree to save.
     * @param lineWidth The width of the image separation lines.
     */
    private static void saveTree(String filename, String treeType, Tree tree, int lineWidth) {
        FileModule.saveToTXT(filename, treeType, tree);
        FileModule.saveToPNG(filename, tree.toImage(lineWidth));
    }
}
