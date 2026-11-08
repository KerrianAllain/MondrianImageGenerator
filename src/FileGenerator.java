import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FileGenerator {
    private static final List<Color> COLORS = Collections.unmodifiableList(
            Arrays.asList(null, Color.RED, Color.BLUE, Color.BLACK, Color.WHITE, Color.YELLOW));
    private static final Random RANDOM = new Random();
    private final int fileLength;

    /**
     * Generate a file with a specific length.
     *
     * @param fileLength The length of the file.
     */
    public FileGenerator(int fileLength) {
        this.fileLength = fileLength;
    }

    /**
     * Generate the content of an input file.
     *
     * @return A list of LineData to build an Image from.
     */
    public List<LineData> generateFile() {
        List<LineData> content = new ArrayList<>();

        for (int k = 1; k <= this.fileLength; k++)
            content.add(this.generateLineData(k));
        return content;
    }

    /**
     * Generate a LineData content.
     *
     * @param k The current line index being generated.
     * @return The generated LineData from scratch.
     */
    private LineData generateLineData(int k) {
        return new LineData(k,
                this.generateDouble(0.0, 1.0),
                this.generateDouble(0.0, 1.0),
                this.pickAColor(),
                this.pickAColor());
    }

    /**
     * Generate a double value.
     *
     * @param min The min double value (included).
     * @param max The max double value (excluded).
     * @return The generated double value, rounded to 2 decimal places.
     */
    private double generateDouble(double min, double max) {
        double value = min + RANDOM.nextDouble() * (max - min);
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * Pick a Color from the available ones (same, red, blue, black, white, yellow).
     *
     * @return The picked color.
     */
    private Color pickAColor() {
        return COLORS.get(this.generateInt(0, COLORS.size() - 1));
    }

    /**
     * Generate an int value.
     *
     * @param min The min int value (included).
     * @param max The max int value (included).
     * @return The generated int value.
     */
    private int generateInt(int min, int max) {
        return RANDOM.nextInt(max - min + 1) + min;
    }
}
