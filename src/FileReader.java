
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FileReader {

    private final Path filePath;

    public FileReader(String path) {
        this.filePath = this.tryLoadFile(path);
    }

    /**
     * Load the file from the given path. It must be a valid text file.
     *
     * <p>
     * <b>Complexity: O(n)</b>, where {@code n} is the length of the path.
     * </p>
     *
     * @param path The path to the file to load.
     * @return The loaded file.
     */
    private Path tryLoadFile(String path) {
        try {
            Objects.requireNonNull(path, "File path cannot be null.");

            Path file = Paths.get(path);
            String filename = file.getFileName().toString();
            if (!Files.isRegularFile(file) || !filename.endsWith(".txt")) {
                throw new IllegalArgumentException("Path: " + path + " is not a valid text file.");
            }
            return file;
        } catch (IllegalArgumentException exception) {
            Utils.logException(exception);
            System.exit(1);
            return null;
        }
    }

    /**
     * Read the file content and build a list of line data for each line read.
     *
     * <p>
     * <b>Complexity: O(n)</b>, where {@code n} is the rows number in the file.
     * </p>
     *
     * @return Each file line into a LineData class to use to build the tree.
     */
    public List<LineData> readFile() {
        List<LineData> content = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(this.filePath)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    content.add(new LineData(line));
                }
            }
            return content;
        } catch (IOException | IllegalArgumentException exception) {
            Utils.logException(exception);
            System.exit(1);
            return Collections.emptyList();
        }
    }

    /**
     * Read a serialized tree from a text file.
     *
     * <p>
     * <b>Complexity: O(n)</b>, where {@code n} is the file size.
     * </p>
     *
     * @return The trimmed tree representation.
     */
    public String readTreeText() {
        try {
            return new String(Files.readAllBytes(this.filePath), StandardCharsets.UTF_8).trim();
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to read tree file '" + this.filePath.toString() + "'.", exception);
        }
    }
}
