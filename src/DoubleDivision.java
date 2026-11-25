public class DoubleDivision {
    public final int xValue;
    public final int yValue;

    /**
     * Determine the axis and the position to divide the segment.
     *
     * <p>
     * <b>Complexity: O(1)</b>.
     * </p>
     *
     * @param topLeft The top left coordinates.
     * @param line    The line data to access ratios {@code p} and {@code q}.
     * @param tree    The target RQuadtree instance.
     */
    public DoubleDivision(Point topLeft, LineData line, RQuadtree tree) {
        this.yValue = topLeft.y + (int) (tree.height() * line.q);
        this.xValue = topLeft.x + (int) (tree.width() * line.p);
    }
}
