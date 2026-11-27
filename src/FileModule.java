import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileModule {
    private static final Path TARGET_DIR = Paths.get("AllainAllain", "target");

    /**
     * Save an ImagePNG to a PNG file in a subfolder inside the TARGET directory.
     *
     * <p>
     * <b>Complexity:</b> depends on {@link Image#save(String)}.
     * </p>
     *
     * @param filename The file name without the extension.
     * @param image    The image to save.
     */
    public static void saveToPNG(String filename, Image image) {
        Path outputDirectory = TARGET_DIR.resolve(filename);
        Path targetPath = outputDirectory.resolve(filename + ".png");

        if (!createTargetDirectory(outputDirectory))
            return;

        try {
            image.save(targetPath.toString());
            Utils.logSuccess(targetPath + " saved successfully.");
        } catch (IOException exception) {
            Utils.logError("An IO exception occurred while saving the image: " + exception.getMessage());
        }
    }

    /**
     * Save a tree's string representation to a UTF-8 text file.
     *
     * <p><b>Complexity: O(n)</b>, where {@code n} is the representation length.</p>
     *
     * @param filename The filename without the extension.
     * @param treeType The tree type to append to the filename.
     * @param tree The tree to save.
     */
    public static void saveToTXT(String filename, String treeType, Tree tree) {
        Path outputDirectory = TARGET_DIR.resolve(filename);
        Path targetPath = outputDirectory.resolve(filename + "-" + treeType + ".txt");

        if (!createTargetDirectory(outputDirectory))
            return;

        try {
            Files.write(targetPath, tree.toString().getBytes(StandardCharsets.UTF_8));
            Utils.logSuccess(targetPath + " saved successfully.");
        } catch (IOException exception) {
            Utils.logError("An IO exception occurred while saving the tree: " + exception.getMessage());
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
     * @return {@code true} if the directory exists or was created, {@code false}
     *         otherwise.
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
