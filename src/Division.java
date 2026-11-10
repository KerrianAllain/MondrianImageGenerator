public class Division {
    public final Axis axis;
    public final int value;

    /**
     * Determines the axis and the position to divide the segment.
     *
     * <p>
     * <b>Complexity: O(1)</b>.
     * </p>
     *
     * @param axis    The axis to use for the division.
     * @param topLeft The top left coordinates.
     * @param line    The line data to access ratios {@code p} and {@code q}.
     * @param tree    The target Tree2D instance.
     */
    public Division(Axis axis, Point topLeft, LineData line, Tree2D tree) {
        int axisY = topLeft.y + (int) (tree.height() * line.q);
        int axisX = topLeft.x + (int) (tree.width() * line.p);

        this.value = (axis == Axis.X) ? axisX : axisY;
        this.axis = axis;
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
     * @return The calculated split Point.
     */
    public Point getSplitPoint(ChildType childType, Point topLeft, Point bottomRight) {
        Point target = (childType == ChildType.NW) ? bottomRight : topLeft;
        return this.axis.withValue(target, this.value);
    }

    @Override
    public String toString() {
        return this.axis + ", " + this.value;
    }
}
