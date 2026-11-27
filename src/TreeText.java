import java.awt.Color;

final class TreeText {
    private TreeText() {
    }

    static final class Cursor {
        private final String text;
        private int position;

        Cursor(String text) {
            if (text == null)
                throw new IllegalArgumentException("Tree representation cannot be null.");
            this.text = text;
        }

        void expect(String token) {
            if (!this.text.startsWith(token, this.position))
                throw new IllegalArgumentException("Expected '" + token + "' at position " + this.position + ".");
            this.position += token.length();
        }

        boolean startsWith(String token) {
            return this.text.startsWith(token, this.position);
        }

        int readInt() {
            int start = this.position;
            if (this.position < this.text.length() && this.text.charAt(this.position) == '-')
                this.position++;
            int digitsStart = this.position;
            while (this.position < this.text.length() && Character.isDigit(this.text.charAt(this.position)))
                this.position++;
            if (digitsStart == this.position)
                throw new IllegalArgumentException("Expected an integer at position " + start + ".");
            try {
                return Integer.parseInt(this.text.substring(start, this.position));
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Integer out of range at position " + start + ".", exception);
            }
        }

        Color readColor() {
            this.expect("L(");
            int red = this.readInt();
            this.expect(",");
            int green = this.readInt();
            this.expect(",");
            int blue = this.readInt();
            this.expect(")");
            if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255)
                throw new IllegalArgumentException("RGB components must be between 0 and 255.");
            return new Color(red, green, blue);
        }

        void finish() {
            if (this.position != this.text.length())
                throw new IllegalArgumentException("Unexpected content at position " + this.position + ".");
        }
    }
}
