
import java.awt.Color;
import java.util.Objects;
import java.util.function.Consumer;

public final class TreeText {

    private TreeText() {
    }

    @FunctionalInterface
    interface NodeParser<T> {

        T parse(Cursor cursor, Point topLeft, Point bottomRight);
    }

    static <T> T parse(String representation, String prefix, NodeParser<T> parser) {
        Cursor cursor = new Cursor(representation);
        cursor.expect(prefix + "(");

        int width = cursor.readInt();
        cursor.expect(",");
        int height = cursor.readInt();
        cursor.expect(",");

        requirePositiveDimensions(width, height);
        T tree = parser.parse(cursor, new Point(0, 0), new Point(width, height));

        cursor.expect(")");
        cursor.finish();
        return tree;
    }

    static String write(String prefix, int width, int height, Consumer<StringBuilder> nodeWriter) {
        requirePositiveDimensions(width, height);
        Objects.requireNonNull(nodeWriter, "Node writer cannot be null.");

        StringBuilder builder = new StringBuilder()
                .append(prefix)
                .append("(")
                .append(width)
                .append(",")
                .append(height)
                .append(",");

        nodeWriter.accept(builder);
        return builder.append(")").toString();
    }

    static void appendLeaf(StringBuilder builder, Color color) {
        Objects.requireNonNull(builder, "String builder cannot be null.");
        Objects.requireNonNull(color, "Color cannot be null.");

        builder.append("L(")
                .append(color.getRed())
                .append(",")
                .append(color.getGreen())
                .append(",")
                .append(color.getBlue())
                .append(")");
    }

    private static void requirePositiveDimensions(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Tree dimensions must be positive.");
        }
    }

    public static final class Cursor {

        private final String text;
        private int position;

        public Cursor(String text) {
            this.text = Objects.requireNonNull(text, "Tree representation cannot be null.");
        }

        public void expect(String token) {
            if (!startsWith(token)) {
                throw new IllegalArgumentException("Expected '" + token + "' at position " + position + ".");
            }
            position += token.length();
        }

        public boolean startsWith(String token) {
            return text.startsWith(token, position);
        }

        public int readInt() {
            int start = position;
            if (position < text.length() && text.charAt(position) == '-') {
                position++;
            }

            int digitsStart = position;
            while (position < text.length()
                    && Character.isDigit(text.charAt(position))) {
                position++;
            }

            if (digitsStart == position) {
                throw new IllegalArgumentException("Expected an integer at position " + start + ".");
            }

            try {
                return Integer.parseInt(text.substring(start, position));
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Integer out of range at position " + start + ".", exception);
            }
        }

        public Color readColor() {
            expect("L(");

            int red = readInt();
            expect(",");
            int green = readInt();
            expect(",");
            int blue = readInt();
            expect(")");

            if (!isValidColorComponent(red)
                    || !isValidColorComponent(green)
                    || !isValidColorComponent(blue)) {
                throw new IllegalArgumentException("RGB components must be between 0 and 255.");
            }

            return new Color(red, green, blue);
        }

        public void finish() {
            if (position != text.length()) {
                throw new IllegalArgumentException("Unexpected content at position " + position + ".");
            }
        }

        private boolean isValidColorComponent(int value) {
            return value >= 0 && value <= 255;
        }
    }
}
