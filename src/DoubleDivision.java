public class DoubleDivision {
    public final int XValue;
    public final int YValue;


    /**
     * Determines the axis and the position to divide the segment.
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
        this.YValue = topLeft.y + (int) (tree.height() * line.q);
        this.XValue = topLeft.x + (int) (tree.width() * line.p);
    }

    /**
     * Determines the Point to use for the division.
     *
     * <p>
     * <b>Complexity: O(1)</b>.
     * </p>
     *
     * @param childType   The child type to determine.
     * @param topLeft     The top left Point.
     * @param bottomRight The bottom right Point.
     * @return Both calculated split Points.
     */
    public Point getSplitPoint(ChildType childType, Point topLeft, Point bottomRight) {
        // TODO : Need to delete this
        return null;
    }

    @Override
    public String toString() {
        return this.XValue + ", " + this.YValue;
    }
}
