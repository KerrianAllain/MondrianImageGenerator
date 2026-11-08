import java.util.Random;

/**
 * All Utils functions are in {@code O(1)} time.
 * As there is only comparisons and calculations.
 */
public class Utils {
    private static final String GREEN = "\033[32m";
    private static final String RESET = "\033[0m";
    private static final String RED = "\033[31m";

    /**
     * Determines the Axis to divide on based on the variant and leaf dimensions.
     *
     * @param v The variant used for the segmentation.
     * @param w The width of the leaf.
     * @param h The height of the leaf.
     * @return The axis to divide on.
     */
    public static Axis determineAxis(Variant v, int width, int height) {
        double inverseRatio = (double) height / width;
        double ratio = (double) width / height;

        if (v == Variant.B)
            return (ratio > 1) ? Axis.X : Axis.Y;

        if (v == Variant.A)
            return (ratio > 2 || inverseRatio <= 2) ? Axis.X : Axis.Y;

        throw new IllegalArgumentException("Unsupported variant: " + v + ".");
    }

    /**
     * Generate a double value.
     *
     * @param min The min double value (included).
     * @param max The max double value (excluded).
     * @return The generated double value, rounded to 2 decimal places.
     */
    public static double generateDouble(Random RANDOM, double min, double max) {
        return min + RANDOM.nextDouble() * (max - min);
    }

    /**
     * Generate an int value.
     *
     * @param min The min int value (included).
     * @param max The max int value (included).
     * @return The generated int value.
     */
    public static int generateInt(Random RANDOM, int min, int max) {
        return RANDOM.nextInt(max - min + 1) + min;
    }

    /**
     * Rounds an integer to the nearest ten.
     *
     * @param value The value to round.
     * @return The rounded value.
     */
    public static int roundToNearestTen(int value) {
        return Math.round(value / 10.0f) * 10;
    }

    /**
     * Determines the point with the highest left coordinate following the
     * lexicographic order.
     * Reminder: (a, b) < (a', b') iff either a < a' or a = 'a and b < b'.
     *
     * @param p1 The first Point.
     * @param p2 The second Point.
     * @return -1 if the first point matches the previous contract, 1 otherwise.
     */
    public static int highestLeftCoordinate(Point p1, Point p2) {
        int xCompare = Integer.compare(p1.x, p2.x);
        if (xCompare != 0)
            return (xCompare < 0) ? -1 : 1;

        int yCompare = Integer.compare(p1.y, p2.y);
        return yCompare < 0 ? -1 : 1;
    }

    /**
     * Determines the Tree2D having the maximum weight value between two Tree2D.
     *
     * @param left  The left Tree2D.
     * @param right The right Tree2D.
     * @return The tree with the highest weight or {@code null} if their weight
     *         values are equals.
     */
    public static Tree2D maxWeightValue(Tree2D left, Tree2D right) {
        double rightWeight = getWeight(right.width(), right.height());
        double leftWeight = getWeight(left.width(), left.height());

        if (Double.compare(leftWeight, rightWeight) == 0)
            return null;

        return (leftWeight > rightWeight) ? left : right;
    }

    /**
     * Determines the weight of the leaf.
     *
     * @param w The width of the leaf.
     * @param h The height of the leaf.
     * @return The leaf's weight.
     */
    private static double getWeight(int w, int h) {
        return (w * h) / Math.pow(w + h, 1.5);
    }

    /**
     * Displays a success message for a completed action.
     *
     * @param action The description of the successfully completed operation.
     */
    public static void logSuccess(String action) {
        System.out.printf("%s[SUCCESS]%s %s%n", GREEN, RESET, action);
    }

    /**
     * Displays an error message for an action.
     *
     * @param error The error that occurred during the operation.
     */
    public static void logError(String error) {
        System.out.printf("%s[ERROR]%s %s%n", RED, RESET, error);
    }

    /**
     * Displays an error message from an exception.
     *
     * @param exception The exception to display.
     */
    public static void logException(Exception exception) {
        System.out.printf("%s[ERROR]%s %s%n", RED, RESET, exception.getMessage());
    }
}
