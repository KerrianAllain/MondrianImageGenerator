import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FileGenerator {
    // All available colors from the Color module can be used.
    private static final List<Color> COLORS = Collections.unmodifiableList(
            Arrays.asList(
                    null,
                    Color.BLUE,
                    Color.CYAN,
                    Color.DARK_GRAY,
                    Color.GRAY,
                    Color.LIGHT_GRAY,
                    Color.MAGENTA,
                    Color.ORANGE,
                    Color.PINK,
                    Color.RED,
                    Color.WHITE,
                    Color.YELLOW));
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
     * @param dimension The dimension of the LineData (either 2 or 4).
     * @return A list of LineData to build an Image from.
     */
    public List<LineData> generateFile(int dimension) {
        List<LineData> content = new ArrayList<>();
        for (int k = 1; k <= this.fileLength; k++)
            content.add(this.generateLineData(k, dimension));
        return content;
    }

    /**
     * Generate a LineData content.
     *
     * @param k         The current line index being generated.
     * @param dimension The dimension of the LineData (either 2 or 4).
     * @return The generated LineData from scratch.
     */
    private LineData generateLineData(int k, int dimension) {
        Color se = (dimension == 2) ? null : this.pickAColor();
        Color sw = (dimension == 2) ? null : this.pickAColor();
        return new LineData(k,
                Utils.generateDouble(RANDOM, 0.2, 0.8),
                Utils.generateDouble(RANDOM, 0.2, 0.8),
                this.pickAColor(), this.pickAColor(), se, sw);
    }

    /**
     * Pick a Color from the available ones (same, red, blue, black, white, yellow).
     *
     * @return The picked color.
     */
    private Color pickAColor() {
        return COLORS.get(Utils.generateInt(RANDOM, 0, COLORS.size() - 1));
    }
}
