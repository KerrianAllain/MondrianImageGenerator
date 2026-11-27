
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileModule {

    private static final Path TARGET_DIR = Paths.get("AllainAllain", "target");

    /**
     * A consumer that accepts a path and may throw an IOException. This allows
     * to abstract the usage of the save methods.
     */
    @FunctionalInterface
    private interface IOConsumer<T> {

        void accept(T value) throws IOException;
    }

    /**
     * Save an ImagePNG to a PNG file in a subfolder inside the TARGET
     * directory.
     *
     * <p>
     * <b>Complexity:</b> depends on {@link Image#save(String)}.
     * </p>
     *
     * @param filename The file name without the extension.
     * @param image The image to save.
     */
    public static void saveToPNG(String filename, Image image) {
        save(filename, filename + ".png", "image",
                path -> image.save(path.toString()));
    }

    /**
     * Save a tree's string representation to a UTF-8 text file.
     *
     * <p>
     * <b>Complexity: O(n)</b>, where {@code n} is the string length.
     * </p>
     *
     * @param filename The filename without the extension.
     * @param treeType The tree type to append to the filename.
     * @param tree The tree to save.
     */
    public static void saveToTXT(String filename, String treeType, Tree tree) {
        save(filename, filename + "-" + treeType + ".txt", "tree",
                path -> Files.writeString(path, tree.toString(), StandardCharsets.UTF_8));
    }

    /**
     * Save content to a file inside a subfolder of the TARGET directory.
     *
     * @param directoryName The name of the output subfolder.
     * @param filename The name of the output file.
     * @param contentType The type of content being saved.
     * @param writer The operation responsible for writing the content.
     */
    private static void save(String directoryName, String filename, String contentType, IOConsumer<Path> writer) {
        Path outputDirectory = TARGET_DIR.resolve(directoryName);
        Path targetPath = outputDirectory.resolve(filename);

        if (!createTargetDirectory(outputDirectory)) {
            return;
        }

        try {
            writer.accept(targetPath);
            Utils.logSuccess(targetPath + " saved successfully.");
        } catch (IOException exception) {
            Utils.logError("An IO exception occurred while saving the " + contentType + ": " + exception.getMessage());
        }
    }

    /**
     * Create the required target directory if it does not exist.
     *
     * <p>
     * <b>Complexity: O(1)</b>.
     * </p>
     *
     * @param directoryPath The path of the directory to create.
     * @return {@code true} if the directory exists or was created,
     * {@code false} otherwise.
     */
    private static boolean createTargetDirectory(Path directoryPath) {
        try {
            Files.createDirectories(directoryPath);
            return true;
        } catch (IOException exception) {
            Utils.logError("Unable to create directory: " + directoryPath);
            return false;
        }
    }
}
