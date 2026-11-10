import java.awt.Color;
import java.util.List;

public class RQuadtree {
    private final Point bottomRight;
    private final Point topLeft;
    private final Color color;

    private DoubleDivision division;
    private RQuadtree NW;
    private RQuadtree NE;
    private RQuadtree SE;
    private RQuadtree SW;

    public RQuadtree(StrategySpecs specs, List<LineData> linesData) {
        this.bottomRight = new Point(specs.width, specs.height);
        this.topLeft = new Point(0, 0);
        this.color = Color.WHITE;

        this.build(specs.nbLeavesToDivide, specs.minCutDimension, linesData);
    }

    private RQuadtree(Point topLeft, Point bottomRight, Color color) {
        this.bottomRight = bottomRight;
        this.topLeft = topLeft;
        this.color = color;
    }

    /**
     * Builds the RQuadtree from the given data.
     *
     * <p>
     * <b>Complexity: O(n²)</b>, to be more specific, in <b>θ(n * m)</b>,
     * where {@code n} is the number of lines to process,
     * and {@code m} is the complexity of {@link RQuadtree#leafOfMaximumWeight(int)}
     * (which increases at each iteration of loop because of the insertions).
     * </p>
     *
     * @param nbLeavesToDivide The maximum number of leaves authorized.
     * @param minCutDimension  The minimal square cut dimension.
     * @param v                The variant used to represent the RQuadtree.
     * @param lines            The list of lines from the source file.
     */
    private void build(int nbLeavesToDivide, int minCutDimension, List<LineData> lines) {
        int divisions = 0;

        for (LineData line : lines) {
            if (divisions == nbLeavesToDivide)
                break;

            RQuadtree workingLeaf = this.leafOfMaximumWeight(minCutDimension);
            if (workingLeaf.width() < minCutDimension || workingLeaf.height() < minCutDimension)
                break;

            workingLeaf.division = new DoubleDivision(workingLeaf.topLeft, line, workingLeaf);

            workingLeaf.NW = workingLeaf.buildLeaf(line.nw, ChildType.NW);
            workingLeaf.NE = workingLeaf.buildLeaf(line.ne, ChildType.NE);
            workingLeaf.SE = workingLeaf.buildLeaf(line.se, ChildType.SE);
            workingLeaf.SW = workingLeaf.buildLeaf(line.sw, ChildType.SW);

            divisions++;
        }
    }

    /**
     * Builds a child RQuadtree leaf based on the split geometry and child type.
     *
     * <p>
     * <b>Complexity: O(1)</b>.
     * </p>
     *
     * @param childColor Specified color for the child.
     * @param childType  Whether this is a LEFT or RIGHT child node.
     * @return A new RQuadtree instance representing the split child leaf.
     */
    private RQuadtree buildLeaf(Color childColor, ChildType childType) {
        Color nextColor = (childColor == null) ? this.color : childColor;
        switch (childType) {
            case NW:
                return new RQuadtree(this.topLeft, new Point(this.division.XValue, this.division.YValue), nextColor);

            case NE:
                return new RQuadtree(new Point(this.division.XValue, this.topLeft.y), new Point(this.bottomRight.x, this.division.YValue), nextColor);

            case SE:
                return new RQuadtree(new Point(this.division.XValue, this.division.YValue), this.bottomRight, nextColor);

            case SW:
                return new RQuadtree(new Point(this.topLeft.x, this.division.YValue), new Point(this.division.XValue, this.bottomRight.y), nextColor);
            default:
                throw new AssertionError();
        }
    }

    /**
     * @return {@code true} if the tree has no child, {@code false} otherwise.
     */
    private boolean isLeaf() {
        return this.NW == null;
    }

    /**
     * Determines the leaf that has the maximum weight and respects minCutDimension.
     *
     * <p>
     * <b>Complexity: O(n)</b>, where {@code n} is the number of nodes.
     * </p>
     *
     * @param minCutDimension Minimum allowed width and height for a cut.
     * @return The leaf with the maximum weight, or null if no valid leaf exists.
     */
    private RQuadtree leafOfMaximumWeight(int minCutDimension) {
        if (this.isLeaf()) {
            return this;
        }

        RQuadtree NWMaximum = this.NW.leafOfMaximumWeight(minCutDimension);
        RQuadtree NEMaximum = this.NE.leafOfMaximumWeight(minCutDimension);
        RQuadtree SEMaximum = this.SE.leafOfMaximumWeight(minCutDimension);
        RQuadtree SWMaximum = this.SW.leafOfMaximumWeight(minCutDimension);

        RQuadtree tree = Utils.maxWeightValue(NWMaximum, NEMaximum, SEMaximum, SWMaximum);
        if (tree != null)
            return tree;

        return NWMaximum;
    }

    /**
     * Builds the image representation of the tree.
     *
     * <p>
     * <b>Complexity: O(n)</b>, where {@code n} is the number of pixels.
     * </p>
     *
     * @param lineWidth The line width of separation between rectangles.
     * @return The resulting image.
     */
    public Image toImage(int lineWidth) {
        Image image = new Image(this.width(), this.height());
        return this.colorImage(image, lineWidth);
    }

    /**
     * Colors an image a rectangle at a time.
     *
     * <p>
     * <b>Complexity: O(n)</b>, where {@code n} is the number of pixels.
     * </p>
     *
     * @param image     The image to progressively color.
     * @param lineWidth The line width of separation between rectangles.
     * @return The colored image.
     */
    private Image colorImage(Image image, int lineWidth) {
        if (this.isLeaf()) {
            image.setRectangle(this.topLeft.x, this.bottomRight.x, this.topLeft.y, this.bottomRight.y, this.color);
            return image;
        }

        image = this.NW.colorImage(image, lineWidth);
        image = this.NE.colorImage(image, lineWidth);
        image = this.SE.colorImage(image, lineWidth);
        image = this.SW.colorImage(image, lineWidth);

        if (lineWidth > 0)
            this.drawSeparationLine(image, lineWidth);

        return image;
    }

    /**
     * Draws a line of separation between rectangles.
     *
     * <p>
     * <b>Complexity: depends on
     * {@link Image#setRectangle(int, int, int, int, Color)}</b>.
     * </p>
     *
     * @param image     The image to add a separation line.
     * @param lineWidth The width of the separation line.
     */
    private void drawSeparationLine(Image image, int lineWidth) {
        int halfWidth = lineWidth / 2;
        int restWidth = lineWidth - halfWidth;

        Point XStart = new Point(this.division.XValue - halfWidth, this.topLeft.y);
        Point XEnd = new Point(this.division.XValue + restWidth, this.bottomRight.y);

        Point YStart = new Point(this.topLeft.x, this.division.YValue - halfWidth);
        Point YEnd = new Point(this.bottomRight.x, this.division.YValue + restWidth);

        image.setRectangle(XStart.x, XEnd.x, XStart.y, XEnd.y, Color.BLACK);
        image.setRectangle(YStart.x, YEnd.x, YStart.y, YEnd.y, Color.BLACK);
    }

    public int width() {
        return this.bottomRight.x - this.topLeft.x;
    }

    public int height() {
        return this.bottomRight.y - this.topLeft.y;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (this.NW != null)
            sb.append("( ").append(this.NW).append(" )");

        Object content = (division != null) ? this.division : this.formatColor();
        sb.append("( ").append(content).append(" )");

        if (this.NE != null)
            sb.append("( ").append(this.NE).append(" )");

        return sb.toString();
    }

    /**
     * Formats the leaf color to its R, G, B representation.
     *
     * <p>
     * <b>Complexity: O(1)</b>.
     * </p>
     *
     * @return The formatted color.
     */
    private String formatColor() {
        return this.color.getRed() + ", " + this.color.getGreen() + ", " + this.color.getBlue();
    }
}
