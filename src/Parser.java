import java.awt.Color;
import java.util.Objects;

public class Parser {
    /**
     * Parse a Color value ("m", "r", "b", "n", "w" or "j").
     *
     * <p>
     * <b>Complexity: O(n)</b>, where {@code n} is the length of the value.
     * </p>
     *
     * @param value The character string value representing a color.
     * @return The corresponding {@link Color}, or {@code null} if "m".
     */
    public static Color parseColor(String value) {
        try {
            Objects.requireNonNull(value, "Color value cannot be null.");

            switch (value) {
                case "m":
                    return null;
                case "r":
                    return Color.RED;
                case "b":
                    return Color.BLUE;
                case "n":
                    return Color.BLACK;
                case "w":
                    return Color.WHITE;
                case "j":
                    return Color.YELLOW;

                default:
                    throw new IllegalArgumentException("Invalid color character.");
            }
        } catch (IllegalArgumentException exception) {
            Utils.logException(exception);
            System.exit(1);
            return null;
        }
    }

    /**
     * Parse a coordinate value bounded between 0.2 and 0.8 included.
     *
     * <p>
     * <b>Complexity: O(n)</b>, where {@code n} is the length of the value.
     * </p>
     *
     * @param value The coordinate string to parse.
     * @return The parsed double coordinate.
     */
    public static double parseCoordinate(String value) {
        try {
            double coordinate = Double.parseDouble(value);
            if (coordinate < 0.2 || coordinate > 0.8)
                throw new IllegalArgumentException("Coordinate out of bounds [0.2; 0.8].");
            return coordinate;
        } catch (IllegalArgumentException exception) {
            Utils.logException(exception);
            System.exit(1);
            return -1.0;
        }
    }

    /**
     * Parse a strictly positive integer parameter.
     *
     * <p>
     * <b>Complexity: O(n)</b>, where {@code n} is the length of the value.
     * </p>
     *
     * @param value       The string value to parse.
     * @param description Parameter name for error reporting.
     * @return The parsed integer.
     */
    public static int parseIntParameter(String value, String description) {
        try {
            int parameter = Integer.parseInt(value);
            if (parameter <= 0)
                throw new IllegalArgumentException("Invalid parameter for `" + description + "` (must be positive).");
            return parameter;
        } catch (IllegalArgumentException exception) {
            Utils.logException(exception);
            System.exit(1);
            return -1;
        }
    }

    /**
     * Parse a variant value.
     *
     * <p>
     * <b>Complexity: O(n)</b>, where {@code n} is the length of the value.
     * </p>
     *
     * @param value The variant string to parse.
     * @return The corresponding {@link Variant}.
     */
    public static Variant parseVariant(String value) throws NullPointerException {
        try {
            Objects.requireNonNull("Variant cannot be null.");
            return Variant.valueOf(value);
        } catch (IllegalArgumentException exception) {
            Utils.logException(exception);
            System.exit(1);
            return null;
        }
    }
}
