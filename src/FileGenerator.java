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
                Utils.generateDouble(RANDOM, 0.0, 1.0),
                Utils.generateDouble(RANDOM, 0.0, 1.0),
                this.pickAColor(),
                this.pickAColor());
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
